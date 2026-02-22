package tn.esprit.projetPi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import tn.esprit.projetPi.entities.OrderType;
import java.math.BigDecimal;

@Data
public class CartItemDTO {
    private String id;
    
    @NotBlank(message = "Product ID is required")
    private String productId;
    private String productName;
    private BigDecimal price;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
    
    private String image;
    private OrderType type;
    private Integer rentalDays;
}
