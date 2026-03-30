package tn.esprit.productservice.mapper;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import tn.esprit.productservice.dto.response.CategoryResponse;
import tn.esprit.productservice.dto.response.ProductResponse;
import tn.esprit.productservice.dto.response.ReviewResponse;
import tn.esprit.productservice.entities.Category;
import tn.esprit.productservice.entities.Product;
import tn.esprit.productservice.entities.ProductReview;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO Mapper for Product Service entities.
 *
 * FIX: All lazy-loaded associations are checked with Hibernate.isInitialized()
 * before accessing their fields. This prevents the "could not initialize proxy
 * - no Session" error when the mapper is called outside a transaction.
 */
@Component
public class ProductMapper {

    public ProductResponse toProductResponse(Product entity) {
        if (entity == null) return null;

        // FIX: Only access category fields if the proxy is already initialized.
        Category category = entity.getCategory();
        boolean categoryLoaded = category != null && Hibernate.isInitialized(category);

        return ProductResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .originalPrice(entity.getOriginalPrice())
                .discountPercentage(entity.getDiscountPercentage())
                .sku(entity.getSku())
                .barcode(entity.getBarcode())
                .brand(entity.getBrand())
                .categoryId(categoryLoaded ? category.getId() : null)
                .categoryName(categoryLoaded ? category.getName() : null)
                .sellerId(entity.getSellerId())
                .stockQuantity(entity.getStockQuantity())
                .minStockLevel(entity.getMinStockLevel())
                .images(entity.getImages())
                .thumbnail(entity.getThumbnail())
                .rating(entity.getRating())
                .reviewCount(entity.getReviewCount())
                .salesCount(entity.getSalesCount())
                .viewCount(entity.getViewCount())
                .isActive(entity.getIsActive())
                .isFeatured(entity.getIsFeatured())
                .isOnSale(entity.getIsOnSale())
                .isRentable(entity.getIsRentable())
                .rentalPricePerDay(entity.getRentalPricePerDay())
                .tags(entity.getTags())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<ProductResponse> toProductResponseList(List<Product> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(this::toProductResponse).collect(Collectors.toList());
    }

    // ── Category Mapping ──────────────────────────────────────────────────────

    public CategoryResponse toCategoryResponse(Category entity) {
        if (entity == null) return null;

        // FIX: Guard parent proxy access
        Category parent = entity.getParent();
        boolean parentLoaded = parent != null && Hibernate.isInitialized(parent);

        // FIX: Only call .size() if the collection is initialized
        int productCount = 0;
        if (entity.getProducts() != null && Hibernate.isInitialized(entity.getProducts())) {
            productCount = entity.getProducts().size();
        }

        return CategoryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .slug(entity.getSlug())
                .image(entity.getImage())
                .parentId(parentLoaded ? parent.getId() : null)
                .parentName(parentLoaded ? parent.getName() : null)
                .displayOrder(entity.getDisplayOrder())
                .productCount(productCount)
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<CategoryResponse> toCategoryResponseList(List<Category> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(this::toCategoryResponse).collect(Collectors.toList());
    }

    // ── Review Mapping ────────────────────────────────────────────────────────

    public ReviewResponse toReviewResponse(ProductReview entity) {
        if (entity == null) return null;

        // FIX: Guard product proxy on review
        Product product = entity.getProduct();
        boolean productLoaded = product != null && Hibernate.isInitialized(product);

        return ReviewResponse.builder()
                .id(entity.getId())
                .productId(productLoaded ? product.getId() : null)
                .productName(productLoaded ? product.getName() : null)
                .userId(entity.getUserId())
                .userName(entity.getUserName())
                .rating(entity.getRating())
                .title(entity.getTitle())
                .comment(entity.getComment())
                .images(entity.getImages())
                .helpfulCount(entity.getHelpfulCount())
                .isVerifiedPurchase(entity.getIsVerifiedPurchase())
                .isApproved(entity.getIsApproved())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<ReviewResponse> toReviewResponseList(List<ProductReview> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(this::toReviewResponse).collect(Collectors.toList());
    }
}