package tn.esprit.projetPi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private String id;
    private String name;
    private String description;
    private String shortDescription;
    private String sku;
    private String barcode;
    
    private String sellerId;
    private String sellerName;
    
    private String categoryId;
    private String categoryName;
    private String subcategoryId;
    private String subcategoryName;
    private List<String> tags;

    private BigDecimal price;
    private BigDecimal costPrice;
    private BigDecimal compareAtPrice;
    private BigDecimal rentalPricePerDay;
    private BigDecimal rentalDeposit;
    private Integer discount;
    private BigDecimal discountedPrice;
    
    private Integer stockQuantity;
    private Integer availableStock;
    private Integer lowStockThreshold;
    private Boolean inStock;
    
    private List<String> images;
    private String mainImage;
    private List<String> videos;
    
    private Boolean isActive;
    private Boolean isApproved;
    private Boolean isFeatured;
    private Boolean rentalAvailable;
    
    private Double averageRating;
    private Integer reviewCount;
    
    private Integer loyaltyPoints;
    
    private String seoTitle;
    private String seoDescription;
    private String slug;
    
    private List<ProductSpecDTO> specs;
    private List<ProductVariantDTO> variants;
    
    private Double weight;
    private String weightUnit;
    
    private Integer viewCount;
    private Integer purchaseCount;
    private Integer wishlistCount;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
