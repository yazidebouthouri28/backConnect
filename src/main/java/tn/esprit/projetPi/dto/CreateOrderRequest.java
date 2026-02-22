package tn.esprit.projetPi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import tn.esprit.projetPi.entities.OrderType;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateOrderRequest {
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;
    
    private OrderType type = OrderType.PURCHASE;
    
    @NotEmpty(message = "Order items are required")
    private List<OrderItemDTO> items;
    
    // For rental orders
    private LocalDateTime rentalStartDate;
    private LocalDateTime rentalEndDate;
}
