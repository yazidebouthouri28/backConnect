package tn.esprit.productservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private BigDecimal discountPercentage;
    private String sku;
    private String barcode;
    private String brand;
    private UUID categoryId;
    private String categoryName;
    private UUID sellerId;
    private Integer stockQuantity;
    private Integer minStockLevel;
    private List<String> images;
    private String thumbnail;
    private BigDecimal rating;
    private Integer reviewCount;
    private Integer salesCount;
    private Integer viewCount;
    private Boolean isActive;
    private Boolean isFeatured;
    private Boolean isOnSale;
    private Boolean isRentable;
    private BigDecimal rentalPricePerDay;
    private List<String> tags;

    // Formater les dates pour JSON
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}