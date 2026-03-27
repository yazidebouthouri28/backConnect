package tn.esprit.orderservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO representing product data fetched from Product Service via WebClient.
 * Used for cart operations and order creation to get current product info.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private UUID id;
    private String name;
    private BigDecimal price;
    private String sku;
    private String thumbnail;
    private Integer stockQuantity;
    private Boolean isActive;
}
