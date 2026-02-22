package tn.esprit.projetPi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tn.esprit.projetPi.dto.*;
import tn.esprit.projetPi.entities.Product;
import tn.esprit.projetPi.entities.ProductSpec;
import tn.esprit.projetPi.entities.ProductVariant;
import tn.esprit.projetPi.entities.User;
import tn.esprit.projetPi.exception.ResourceNotFoundException;
import tn.esprit.projetPi.repositories.CategoryRepository;
import tn.esprit.projetPi.repositories.ProductRepository;
import tn.esprit.projetPi.repositories.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public PageResponse<ProductDTO> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> productPage = productRepository.findAll(pageable);
        return toPageResponse(productPage);
    }

    public PageResponse<ProductDTO> getActiveProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> productPage = productRepository.findByIsActive(true, pageable);
        return toPageResponse(productPage);
    }

    public PageResponse<ProductDTO> getPendingApprovalProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<Product> productPage = productRepository.findByIsApproved(false, pageable);
        return toPageResponse(productPage);
    }

    public ProductDTO getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        // Increment view count
        product.setViewCount((product.getViewCount() != null ? product.getViewCount() : 0) + 1);
        productRepository.save(product);
        
        return toDTO(product);
    }

    public PageResponse<ProductDTO> getProductsBySeller(String sellerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> productPage = productRepository.findBySellerId(sellerId, pageable);
        return toPageResponse(productPage);
    }

    public PageResponse<ProductDTO> getProductsByCategory(String categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> productPage = productRepository.findByCategoryId(categoryId, pageable);
        return toPageResponse(productPage);
    }

    public PageResponse<ProductDTO> searchProducts(ProductSearchRequest request) {
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 20;
        
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "createdAt";
        Sort.Direction direction = "asc".equalsIgnoreCase(request.getSortDirection()) ? 
                Sort.Direction.ASC : Sort.Direction.DESC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Product> productPage;

        if (request.getQuery() != null && !request.getQuery().isEmpty()) {
            productPage = productRepository.searchByKeyword(request.getQuery(), pageable);
        } else if (request.getMinPrice() != null && request.getMaxPrice() != null) {
            productPage = productRepository.findByPriceRange(request.getMinPrice(), request.getMaxPrice(), pageable);
        } else {
            productPage = productRepository.findByIsActive(true, pageable);
        }

        return toPageResponse(productPage);
    }

    public List<ProductDTO> getFeaturedProducts() {
        return productRepository.findByIsFeatured(true).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getTopSellingProducts(int limit) {
        return productRepository.findTop10ByOrderByPurchaseCountDesc().stream()
                .limit(limit)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getTopRatedProducts(int limit) {
        return productRepository.findTop10ByOrderByAverageRatingDesc().stream()
                .limit(limit)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO createProduct(String sellerId, ProductDTO request) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found: " + sellerId));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setShortDescription(request.getShortDescription());
        product.setSku(request.getSku() != null ? request.getSku() : generateSku());
        product.setBarcode(request.getBarcode());
        
        product.setSellerId(sellerId);
        product.setSellerName(seller.getStoreName() != null ? seller.getStoreName() : seller.getName());
        
        product.setCategoryId(request.getCategoryId());
        product.setCategoryName(request.getCategoryName());
        product.setSubcategoryId(request.getSubcategoryId());
        product.setSubcategoryName(request.getSubcategoryName());
        product.setTags(request.getTags() != null ? request.getTags() : new ArrayList<>());
        
        product.setPrice(request.getPrice());
        product.setCostPrice(request.getCostPrice());
        product.setCompareAtPrice(request.getCompareAtPrice());
        product.setRentalPricePerDay(request.getRentalPricePerDay());
        product.setRentalDeposit(request.getRentalDeposit());
        product.setDiscount(request.getDiscount());
        
        // Calculate discounted price
        if (request.getDiscount() != null && request.getDiscount() > 0) {
            BigDecimal discountAmount = request.getPrice()
                    .multiply(BigDecimal.valueOf(request.getDiscount()))
                    .divide(BigDecimal.valueOf(100));
            product.setDiscountedPrice(request.getPrice().subtract(discountAmount));
        }
        
        product.setStockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0);
        product.setReservedQuantity(0);
        updateAvailableStock(product);
        product.setLowStockThreshold(request.getLowStockThreshold() != null ? request.getLowStockThreshold() : 10);
        product.setTrackInventory(true);
        product.setAllowBackorder(false);
        
        product.setImages(request.getImages() != null ? request.getImages() : new ArrayList<>());
        product.setMainImage(request.getMainImage());
        product.setVideos(request.getVideos());
        
        product.setIsActive(true);
        product.setIsApproved(false); // Requires admin approval
        product.setIsFeatured(false);
        product.setRentalAvailable(request.getRentalAvailable() != null ? request.getRentalAvailable() : false);
        
        product.setAverageRating(0.0);
        product.setReviewCount(0);
        product.setLoyaltyPoints(request.getLoyaltyPoints());
        
        product.setSeoTitle(request.getSeoTitle());
        product.setSeoDescription(request.getSeoDescription());
        product.setSlug(generateSlug(request.getName()));
        
        if (request.getSpecs() != null) {
            product.setSpecs(request.getSpecs().stream().map(this::toSpec).collect(Collectors.toList()));
        }
        if (request.getVariants() != null) {
            product.setVariants(request.getVariants().stream().map(this::toVariant).collect(Collectors.toList()));
        }
        
        product.setWeight(request.getWeight());
        product.setWeightUnit(request.getWeightUnit());
        
        product.setViewCount(0);
        product.setPurchaseCount(0);
        product.setWishlistCount(0);
        
        product.setCreatedAt(LocalDateTime.now());

        Product saved = productRepository.save(product);
        log.info("Product created: {} by seller {}", saved.getId(), sellerId);
        return toDTO(saved);
    }

    public ProductDTO updateProduct(String id, ProductDTO request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getShortDescription() != null) product.setShortDescription(request.getShortDescription());
        if (request.getBarcode() != null) product.setBarcode(request.getBarcode());
        if (request.getCategoryId() != null) product.setCategoryId(request.getCategoryId());
        if (request.getCategoryName() != null) product.setCategoryName(request.getCategoryName());
        if (request.getTags() != null) product.setTags(request.getTags());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getCostPrice() != null) product.setCostPrice(request.getCostPrice());
        if (request.getCompareAtPrice() != null) product.setCompareAtPrice(request.getCompareAtPrice());
        if (request.getRentalPricePerDay() != null) product.setRentalPricePerDay(request.getRentalPricePerDay());
        if (request.getRentalDeposit() != null) product.setRentalDeposit(request.getRentalDeposit());
        if (request.getDiscount() != null) {
            product.setDiscount(request.getDiscount());
            if (request.getDiscount() > 0 && product.getPrice() != null) {
                BigDecimal discountAmount = product.getPrice()
                        .multiply(BigDecimal.valueOf(request.getDiscount()))
                        .divide(BigDecimal.valueOf(100));
                product.setDiscountedPrice(product.getPrice().subtract(discountAmount));
            }
        }
        if (request.getStockQuantity() != null) {
            product.setStockQuantity(request.getStockQuantity());
            updateAvailableStock(product);
        }
        if (request.getLowStockThreshold() != null) product.setLowStockThreshold(request.getLowStockThreshold());
        if (request.getImages() != null) product.setImages(request.getImages());
        if (request.getMainImage() != null) product.setMainImage(request.getMainImage());
        if (request.getIsActive() != null) product.setIsActive(request.getIsActive());
        if (request.getRentalAvailable() != null) product.setRentalAvailable(request.getRentalAvailable());
        if (request.getLoyaltyPoints() != null) product.setLoyaltyPoints(request.getLoyaltyPoints());
        if (request.getSeoTitle() != null) product.setSeoTitle(request.getSeoTitle());
        if (request.getSeoDescription() != null) product.setSeoDescription(request.getSeoDescription());
        if (request.getSpecs() != null) {
            product.setSpecs(request.getSpecs().stream().map(this::toSpec).collect(Collectors.toList()));
        }
        if (request.getVariants() != null) {
            product.setVariants(request.getVariants().stream().map(this::toVariant).collect(Collectors.toList()));
        }
        
        product.setUpdatedAt(LocalDateTime.now());

        return toDTO(productRepository.save(product));
    }

    public ProductDTO approveProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setIsApproved(true);
        product.setPublishedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        log.info("Product {} approved", id);
        return toDTO(productRepository.save(product));
    }

    public ProductDTO rejectProduct(String id, String reason) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setIsApproved(false);
        product.setIsActive(false);
        product.setUpdatedAt(LocalDateTime.now());
        log.info("Product {} rejected: {}", id, reason);
        return toDTO(productRepository.save(product));
    }

    public ProductDTO toggleFeatured(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setIsFeatured(!Boolean.TRUE.equals(product.getIsFeatured()));
        product.setUpdatedAt(LocalDateTime.now());
        return toDTO(productRepository.save(product));
    }

    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
        log.info("Product {} deleted", id);
    }

    // Helper methods
    private void updateAvailableStock(Product product) {
        int stock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
        int reserved = product.getReservedQuantity() != null ? product.getReservedQuantity() : 0;
        int available = Math.max(0, stock - reserved);
        product.setAvailableStock(available);
        product.setInStock(available > 0);
    }

    private String generateSku() {
        return "SKU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                + "-" + UUID.randomUUID().toString().substring(0, 4);
    }

    private ProductSpec toSpec(ProductSpecDTO dto) {
        ProductSpec spec = new ProductSpec();
        spec.setKey(dto.getKey());
        spec.setValue(dto.getValue());
        spec.setGroup(dto.getGroup());
        return spec;
    }

    private ProductVariant toVariant(ProductVariantDTO dto) {
        ProductVariant variant = new ProductVariant();
        variant.setId(dto.getId() != null ? dto.getId() : UUID.randomUUID().toString());
        variant.setName(dto.getName());
        variant.setSku(dto.getSku());
        variant.setPrice(dto.getPrice());
        variant.setStockQuantity(dto.getStockQuantity());
        variant.setImage(dto.getImage());
        variant.setColor(dto.getColor());
        variant.setSize(dto.getSize());
        return variant;
    }

    private ProductDTO toDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .shortDescription(product.getShortDescription())
                .sku(product.getSku())
                .barcode(product.getBarcode())
                .sellerId(product.getSellerId())
                .sellerName(product.getSellerName())
                .categoryId(product.getCategoryId())
                .categoryName(product.getCategoryName())
                .subcategoryId(product.getSubcategoryId())
                .subcategoryName(product.getSubcategoryName())
                .tags(product.getTags())
                .price(product.getPrice())
                .costPrice(product.getCostPrice())
                .compareAtPrice(product.getCompareAtPrice())
                .rentalPricePerDay(product.getRentalPricePerDay())
                .rentalDeposit(product.getRentalDeposit())
                .discount(product.getDiscount())
                .discountedPrice(product.getDiscountedPrice())
                .stockQuantity(product.getStockQuantity())
                .availableStock(product.getAvailableStock())
                .lowStockThreshold(product.getLowStockThreshold())
                .inStock(product.getInStock())
                .images(product.getImages())
                .mainImage(product.getMainImage())
                .videos(product.getVideos())
                .isActive(product.getIsActive())
                .isApproved(product.getIsApproved())
                .isFeatured(product.getIsFeatured())
                .rentalAvailable(product.getRentalAvailable())
                .averageRating(product.getAverageRating())
                .reviewCount(product.getReviewCount())
                .loyaltyPoints(product.getLoyaltyPoints())
                .seoTitle(product.getSeoTitle())
                .seoDescription(product.getSeoDescription())
                .slug(product.getSlug())
                .specs(product.getSpecs() != null ? product.getSpecs().stream()
                        .map(this::toSpecDTO).collect(Collectors.toList()) : null)
                .variants(product.getVariants() != null ? product.getVariants().stream()
                        .map(this::toVariantDTO).collect(Collectors.toList()) : null)
                .weight(product.getWeight())
                .weightUnit(product.getWeightUnit())
                .viewCount(product.getViewCount())
                .purchaseCount(product.getPurchaseCount())
                .wishlistCount(product.getWishlistCount())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    private ProductSpecDTO toSpecDTO(ProductSpec spec) {
        return ProductSpecDTO.builder()
                .key(spec.getKey())
                .value(spec.getValue())
                .group(spec.getGroup())
                .build();
    }

    private ProductVariantDTO toVariantDTO(ProductVariant variant) {
        return ProductVariantDTO.builder()
                .id(variant.getId())
                .name(variant.getName())
                .sku(variant.getSku())
                .price(variant.getPrice())
                .stockQuantity(variant.getStockQuantity())
                .image(variant.getImage())
                .color(variant.getColor())
                .size(variant.getSize())
                .build();
    }

    private PageResponse<ProductDTO> toPageResponse(Page<Product> page) {
        return PageResponse.<ProductDTO>builder()
                .content(page.getContent().stream().map(this::toDTO).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
