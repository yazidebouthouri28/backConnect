package tn.esprit.projetPi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.projetPi.entities.OrderType;
import tn.esprit.projetPi.entities.PaymentMethod;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {
    @NotEmpty(message = "Order must have at least one item")
    private List<OrderItemDTO> items;
    
    @NotNull(message = "Shipping address is required")
    private ShippingAddressDTO shippingAddress;
    
    private ShippingAddressDTO billingAddress;
    
    private OrderType type;
    
    private LocalDateTime rentalStartDate;
    private LocalDateTime rentalEndDate;
    
    private String couponCode;
    private PaymentMethod paymentMethod;
    private String customerNotes;
}
