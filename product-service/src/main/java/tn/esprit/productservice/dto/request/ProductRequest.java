package tn.esprit.productservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(min = 2, max = 200)
    private String name;

    @Size(max = 2000)
    private String description;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.01")
    @DecimalMax(value = "9999999.99")
    private BigDecimal price;

    @DecimalMin(value = "0.00")
    private BigDecimal originalPrice;

    @DecimalMin(value = "0.00")
    @DecimalMax(value = "100.00")
    private BigDecimal discountPercentage;

    @Size(max = 50)
    private String sku;

    @Size(max = 50)
    private String barcode;

    @Size(max = 100)
    private String brand;

    private UUID categoryId;

    /** Seller ID from User Service (extracted from JWT via API Gateway) */
    private UUID sellerId;

    @Min(value = 0)
    private Integer stockQuantity;

    @Min(value = 0)
    private Integer minStockLevel;

    @Min(value = 1)
    private Integer maxStockLevel;

    private Boolean trackInventory;

    private List<String> images;

    @Size(max = 500)
    private String thumbnail;

    private Boolean isFeatured;
    private Boolean isOnSale;
    private Boolean isRentable;

    @DecimalMin(value = "0.00")
    private BigDecimal rentalPricePerDay;

    @DecimalMin(value = "0.00")
    private Double weight;

    @Size(max = 100)
    private String dimensions;

    private List<String> tags;
}
