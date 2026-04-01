package tn.esprit.productservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.productservice.dto.response.ProductResponse;
import tn.esprit.productservice.entities.Category;
import tn.esprit.productservice.entities.Product;
import tn.esprit.productservice.events.ProductEvent;
import tn.esprit.productservice.events.ProductEventPublisher;
import tn.esprit.productservice.exception.ResourceNotFoundException;
import tn.esprit.productservice.mapper.ProductMapper;
import tn.esprit.productservice.repositories.CategoryRepository;
import tn.esprit.productservice.repositories.ProductRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductEventPublisher eventPublisher;
    private final ProductMapper mapper;

    // ── Read Operations ───────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "'active-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public List<ProductResponse> getActiveProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAllActiveWithCategory(pageable);
        return products.map(mapper::toProductResponse).getContent();
    }

    @Transactional(readOnly = true)
//    @Cacheable(value = "product-detail", key = "#id")
    public Product getProductById(UUID id) {
        return productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "'category-' + #categoryId + '-' + #pageable.pageNumber")
    public Page<ProductResponse> getProductsByCategory(UUID categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable)
                .map(mapper::toProductResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsBySeller(UUID sellerId, Pageable pageable) {
        return productRepository.findBySellerId(sellerId, pageable)
                .map(mapper::toProductResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchProducts(keyword, pageable)
                .map(mapper::toProductResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByPriceRange(minPrice, maxPrice, pageable)
                .map(mapper::toProductResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "featured-products")
    public List<ProductResponse> getFeaturedProducts() {
        return mapper.toProductResponseList(productRepository.findByIsFeaturedTrueAndIsActiveTrue());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getTopSellingProducts(int limit) {
        return mapper.toProductResponseList(
                productRepository.findTopSellingProducts(PageRequest.of(0, limit))
        );
    }

    // ── Write Operations ──────────────────────────────────────────────────────

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "products", allEntries = true),
            @CacheEvict(value = "featured-products", allEntries = true)
    })
    public Product createProduct(Product product, UUID categoryId, UUID sellerId) {
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
            product.setCategory(category);
        }
        product.setSellerId(sellerId);
        Product saved = productRepository.save(product);

        // FIX: Kafka publish is fire-and-forget. If Kafka is down, we log the
        // error but do NOT let it fail the HTTP response or roll back the save.
        publishEvent(() -> {
            ProductEvent event = buildProductEvent(saved);
            eventPublisher.publishProductCreated(event);
        }, "product.created", saved.getId());

        return saved;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "products", allEntries = true),
            @CacheEvict(value = "product-detail", key = "#id"),
            @CacheEvict(value = "featured-products", allEntries = true)
    })
    public Product updateProduct(UUID id, Product productDetails) {
        Product product = getProductById(id);
        Map<String, Object> changes = new HashMap<>();

        if (productDetails.getName() != null && !productDetails.getName().equals(product.getName())) {
            changes.put("name", productDetails.getName());
            product.setName(productDetails.getName());
        }
        if (productDetails.getDescription() != null)   product.setDescription(productDetails.getDescription());
        if (productDetails.getPrice() != null && !productDetails.getPrice().equals(product.getPrice())) {
            changes.put("price", productDetails.getPrice());
            product.setPrice(productDetails.getPrice());
        }
        if (productDetails.getOriginalPrice() != null)  product.setOriginalPrice(productDetails.getOriginalPrice());
        if (productDetails.getStockQuantity() != null && !productDetails.getStockQuantity().equals(product.getStockQuantity())) {
            changes.put("stockQuantity", productDetails.getStockQuantity());
            product.setStockQuantity(productDetails.getStockQuantity());
        }
        if (productDetails.getSku() != null)        product.setSku(productDetails.getSku());
        if (productDetails.getBrand() != null)      product.setBrand(productDetails.getBrand());
        if (productDetails.getImages() != null)     product.setImages(productDetails.getImages());
        if (productDetails.getThumbnail() != null)  product.setThumbnail(productDetails.getThumbnail());
        if (productDetails.getIsActive() != null)   product.setIsActive(productDetails.getIsActive());
        if (productDetails.getIsFeatured() != null) product.setIsFeatured(productDetails.getIsFeatured());
        if (productDetails.getIsOnSale() != null)   product.setIsOnSale(productDetails.getIsOnSale());

        Product saved = productRepository.save(product);

        // FIX: fire-and-forget Kafka publish
        final Map<String, Object> finalChanges = changes;
        publishEvent(() -> {
            ProductEvent event = buildProductEvent(saved);
            event.setChanges(finalChanges);
            eventPublisher.publishProductUpdated(event);
        }, "product.updated", saved.getId());

        checkLowStock(saved);
        return saved;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "products", allEntries = true),
            @CacheEvict(value = "product-detail", key = "#id")
    })
    public Product updateStock(UUID id, int quantityChange) {
        Product product = getProductById(id);
        int newStock = Math.max(0, product.getStockQuantity() + quantityChange);
        product.setStockQuantity(newStock);
        Product saved = productRepository.save(product);

        publishEvent(() -> {
            Map<String, Object> changes = new HashMap<>();
            changes.put("stockQuantity", newStock);
            ProductEvent event = buildProductEvent(saved);
            event.setChanges(changes);
            eventPublisher.publishProductUpdated(event);
        }, "stock.updated", saved.getId());

        checkLowStock(saved);
        return saved;
    }

    @Transactional
    public void incrementViewCount(UUID id) {
        Product product = getProductById(id);
        product.setViewCount(product.getViewCount() + 1);
        productRepository.save(product);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "products", allEntries = true),
            @CacheEvict(value = "product-detail", key = "#id"),
            @CacheEvict(value = "featured-products", allEntries = true)
    })
    public void deleteProduct(UUID id) {
        Product product = getProductById(id);

        publishEvent(() -> {
            ProductEvent event = buildProductEvent(product);
            eventPublisher.publishProductDeleted(event);
        }, "product.deleted", product.getId());

        productRepository.deleteById(id);  // ✅ Hard delete
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * FIX: Central fire-and-forget Kafka publisher.
     * Wraps every Kafka send in try/catch so that:
     * - If Kafka is running: event is published normally.
     * - If Kafka is down: error is logged, but the HTTP response succeeds.
     *   The DB transaction is already committed before this runs.
     */
    private void publishEvent(Runnable publishAction, String eventType, UUID productId) {
        try {
            publishAction.run();
        } catch (Exception e) {
            log.error("Failed to publish Kafka event '{}' for product {}. " +
                            "Product was saved successfully. Kafka may be down: {}",
                    eventType, productId, e.getMessage());
        }
    }

    private ProductEvent buildProductEvent(Product product) {
        return ProductEvent.builder()
                .productId(product.getId().toString())
                .productName(product.getName())
                .sku(product.getSku())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .categoryId(product.getCategory() != null ? product.getCategory().getId().toString() : null)
                .sellerId(product.getSellerId() != null ? product.getSellerId().toString() : null)
                .isActive(product.getIsActive())
                .timestamp(LocalDateTime.now())
                .build();
    }

    private void checkLowStock(Product product) {
        if (product.getStockQuantity() != null && product.getMinStockLevel() != null
                && product.getStockQuantity() <= product.getMinStockLevel()) {
            publishEvent(() -> {
                ProductEvent event = buildProductEvent(product);
                eventPublisher.publishLowStock(event);
            }, "inventory.low-stock", product.getId());
            log.warn("Low stock alert for product: {} (stock: {})",
                    product.getName(), product.getStockQuantity());
        }
    }
}