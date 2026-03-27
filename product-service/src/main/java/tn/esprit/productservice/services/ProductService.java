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
import tn.esprit.productservice.entities.Category;
import tn.esprit.productservice.entities.Product;
import tn.esprit.productservice.events.ProductEvent;
import tn.esprit.productservice.events.ProductEventPublisher;
import tn.esprit.productservice.exception.ResourceNotFoundException;
import tn.esprit.productservice.repositories.CategoryRepository;
import tn.esprit.productservice.repositories.ProductRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import tn.esprit.productservice.dto.response.ProductResponse;
import tn.esprit.productservice.mapper.ProductMapper;
import tn.esprit.productservice.mapper.ProductMapper;


/**
 * Product Service - migrated from monolith with added Kafka events and Redis caching.
 * 
 * Kafka Events Published:
 * - product.created: When a new product is created
 * - product.updated: When product details are modified
 * - product.deleted: When a product is soft-deleted
 * - inventory.low-stock: When stock falls below minStockLevel
 * 
 * Redis Caching Strategy:
 * - "products" cache: Paginated product lists (TTL: 5 min)
 * - "product-detail" cache: Individual product lookups (TTL: 10 min)
 * - "featured-products" cache: Featured product list (TTL: 15 min)
 * - Cache is evicted on create/update/delete operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductEventPublisher eventPublisher;
    private final ProductMapper mapper;


    private final ProductMapper productMapper;


    @Cacheable(value = "products", key = "'active-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public List<ProductResponse> getActiveProducts(Pageable pageable) {

        Page<Product> products = productRepository.findAllActiveWithCategory(pageable);

        return products.map(mapper::toProductResponse).getContent();
    }

    @Cacheable(value = "product-detail", key = "#id")
    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Cacheable(value = "products", key = "'category-' + #categoryId + '-' + #pageable.pageNumber")
    public Page<Product> getProductsByCategory(UUID categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }

    public Page<Product> getProductsBySeller(UUID sellerId, Pageable pageable) {
        return productRepository.findBySellerId(sellerId, pageable);
    }

    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchProducts(keyword, pageable);
    }

    public Page<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByPriceRange(minPrice, maxPrice, pageable);
    }

    @Cacheable(value = "featured-products")
    public List<Product> getFeaturedProducts() {
        log.debug("Fetching featured products from DB");
        return productRepository.findByIsFeaturedTrueAndIsActiveTrue();
    }

    public List<Product> getTopSellingProducts(int limit) {
        return productRepository.findTopSellingProducts(PageRequest.of(0, limit));
    }

    /**
     * Create a new product and publish a Kafka event.
     * Cache is evicted for product lists so new product appears.
     */
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

        // Publish Kafka event for product creation
        ProductEvent event = buildProductEvent(saved);
        eventPublisher.publishProductCreated(event);
        log.info("Product created and event published: {} ({})", saved.getName(), saved.getId());

        return saved;
    }

    /**
     * Update product and publish Kafka event with changes map.
     */
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
        if (productDetails.getDescription() != null) product.setDescription(productDetails.getDescription());
        if (productDetails.getPrice() != null && !productDetails.getPrice().equals(product.getPrice())) {
            changes.put("price", productDetails.getPrice());
            product.setPrice(productDetails.getPrice());
        }
        if (productDetails.getOriginalPrice() != null) product.setOriginalPrice(productDetails.getOriginalPrice());
        if (productDetails.getStockQuantity() != null && !productDetails.getStockQuantity().equals(product.getStockQuantity())) {
            changes.put("stockQuantity", productDetails.getStockQuantity());
            product.setStockQuantity(productDetails.getStockQuantity());
        }
        if (productDetails.getSku() != null) product.setSku(productDetails.getSku());
        if (productDetails.getBrand() != null) product.setBrand(productDetails.getBrand());
        if (productDetails.getImages() != null) product.setImages(productDetails.getImages());
        if (productDetails.getThumbnail() != null) product.setThumbnail(productDetails.getThumbnail());
        if (productDetails.getIsActive() != null) product.setIsActive(productDetails.getIsActive());
        if (productDetails.getIsFeatured() != null) product.setIsFeatured(productDetails.getIsFeatured());
        if (productDetails.getIsOnSale() != null) product.setIsOnSale(productDetails.getIsOnSale());

        Product saved = productRepository.save(product);

        // Publish Kafka update event with what changed
        ProductEvent event = buildProductEvent(saved);
        event.setChanges(changes);
        eventPublisher.publishProductUpdated(event);
        log.info("Product updated and event published: {} ({})", saved.getName(), saved.getId());

        // Check for low stock and publish alert if needed
        checkLowStock(saved);

        return saved;
    }

    /**
     * Update stock quantity for a product.
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "products", allEntries = true),
        @CacheEvict(value = "product-detail", key = "#id")
    })
    public Product updateStock(UUID id, int quantityChange) {
        Product product = getProductById(id);
        int newStock = product.getStockQuantity() + quantityChange;
        if (newStock < 0) newStock = 0;
        product.setStockQuantity(newStock);
        Product saved = productRepository.save(product);

        // Publish update event for stock change
        Map<String, Object> changes = new HashMap<>();
        changes.put("stockQuantity", newStock);
        ProductEvent event = buildProductEvent(saved);
        event.setChanges(changes);
        eventPublisher.publishProductUpdated(event);

        // Check for low stock
        checkLowStock(saved);

        return saved;
    }

    @Transactional
    public void incrementViewCount(UUID id) {
        Product product = getProductById(id);
        product.setViewCount(product.getViewCount() + 1);
        productRepository.save(product);
    }

    /**
     * Soft-delete a product and publish Kafka delete event.
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "products", allEntries = true),
        @CacheEvict(value = "product-detail", key = "#id"),
        @CacheEvict(value = "featured-products", allEntries = true)
    })
    public void deleteProduct(UUID id) {
        Product product = getProductById(id);
        product.setIsActive(false);
        productRepository.save(product);

        // Publish Kafka delete event
        ProductEvent event = buildProductEvent(product);
        eventPublisher.publishProductDeleted(event);
        log.info("Product soft-deleted and event published: {} ({})", product.getName(), product.getId());
    }

    // ── Helper Methods ───────────────────────────────────────────────────────

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

    /**
     * Check if product stock is below minimum level and publish low-stock alert.
     */
    private void checkLowStock(Product product) {
        if (product.getStockQuantity() != null && product.getMinStockLevel() != null
                && product.getStockQuantity() <= product.getMinStockLevel()) {
            ProductEvent event = buildProductEvent(product);
            eventPublisher.publishLowStock(event);
            log.warn("Low stock alert for product: {} (stock: {})", product.getName(), product.getStockQuantity());
        }
    }
}
