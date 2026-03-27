package tn.esprit.productservice.mapper;

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
 * Maps entities to response DTOs for API output.
 */
@Component
public class ProductMapper {

    public ProductResponse toProductResponse(Product entity) {
        if (entity == null) return null;
        return ProductResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : null)
                // autres champs
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    public List<ProductResponse> toProductResponseList(List<Product> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(this::toProductResponse).collect(Collectors.toList());
    }

    // ── Category Mapping ─────────────────────────────────────────────────────

    public CategoryResponse toCategoryResponse(Category entity) {
        if (entity == null) return null;
        return CategoryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .slug(entity.getSlug())
                .image(entity.getImage())
                .parentId(entity.getParent() != null ? entity.getParent().getId() : null)
                .parentName(entity.getParent() != null ? entity.getParent().getName() : null)
                .displayOrder(entity.getDisplayOrder())
                .productCount(entity.getProducts() != null ? entity.getProducts().size() : 0)
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<CategoryResponse> toCategoryResponseList(List<Category> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(this::toCategoryResponse).collect(Collectors.toList());
    }

    // ── Review Mapping ───────────────────────────────────────────────────────

    public ReviewResponse toReviewResponse(ProductReview entity) {
        if (entity == null) return null;
        return ReviewResponse.builder()
                .id(entity.getId())
                .productId(entity.getProduct() != null ? entity.getProduct().getId() : null)
                .productName(entity.getProduct() != null ? entity.getProduct().getName() : null)
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
