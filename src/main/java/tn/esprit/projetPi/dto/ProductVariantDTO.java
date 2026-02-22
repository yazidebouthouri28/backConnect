package tn.esprit.projetPi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantDTO {
    private String id;
    private String name;
    private String sku;
    private BigDecimal price;
    private Integer stockQuantity;
    private String image;
    private String color;
    private String size;
}
