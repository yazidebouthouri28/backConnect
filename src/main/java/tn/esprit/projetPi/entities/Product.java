package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "products")
public class Product {

    @Id
    String id;

    // Basic info
    String name;
    String description;
    String shortDescription;
    String sku;
    String barcode;
    
    // Seller info
    String sellerId;
    String sellerName;
    
    // Categorization
    String categoryId;
    String categoryName;
    String subcategoryId;
    String subcategoryName;
    List<String> tags = new ArrayList<>();

    // Pricing
    BigDecimal price;
    BigDecimal costPrice;
    BigDecimal compareAtPrice;
    BigDecimal rentalPricePerDay;
    BigDecimal rentalDeposit;
    Integer discount;
    BigDecimal discountedPrice;
    
    // Stock
    Integer stockQuantity;
    Integer reservedQuantity;
    Integer availableStock; // stockQuantity - reservedQuantity
    Integer lowStockThreshold;
    Boolean inStock;
    Boolean trackInventory;
    
    // Media
    List<String> images = new ArrayList<>();
    String mainImage;
    List<String> videos = new ArrayList<>();
    
    // Features
    Boolean isActive;
    Boolean isApproved; // Admin approval for marketplace
    Boolean isFeatured;
    Boolean rentalAvailable;
    Boolean allowBackorder;
    
    // Ratings & Reviews
    Double averageRating;
    Integer reviewCount;
    
    // Loyalty
    Integer loyaltyPoints;
    
    // SEO
    String seoTitle;
    String seoDescription;
    String slug;
    
    // Specifications
    List<ProductSpec> specs = new ArrayList<>();
    List<ProductVariant> variants = new ArrayList<>();
    
    // Weight & Dimensions for shipping
    Double weight;
    String weightUnit;
    Double length;
    Double width;
    Double height;
    String dimensionUnit;
    
    // Statistics
    Integer viewCount;
    Integer purchaseCount;
    Integer wishlistCount;
    
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    LocalDateTime publishedAt;
}
