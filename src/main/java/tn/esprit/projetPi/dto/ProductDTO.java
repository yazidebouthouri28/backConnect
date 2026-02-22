package tn.esprit.projetPi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDTO {
    private String id;
    
    @NotBlank(message = "Product name is required")
    private String name;
    
    private String description;
    private String shortDescription;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    private BigDecimal rentalPrice;
    private BigDecimal compareAtPrice;
    
    private String sku;
    private List<String> tags;
    private List<String> images;
    
    private Boolean isActive = true;
    private Boolean isFeatured = false;
    private Boolean inStock = true;
    
    private Integer stockQuantity;
    private Integer lowStockThreshold;
    private Boolean rentalAvailable = false;
    
    private Integer loyaltyPoints;
    private Double rating;
    private Integer reviews;
    
    private String seoTitle;
    private String seoDescription;
    
    private Integer discount;
    private List<String> specs;
}
