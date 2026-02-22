package tn.esprit.projetPi.dto;

import lombok.Data;
import tn.esprit.projetPi.entities.OrderStatus;
import tn.esprit.projetPi.entities.OrderType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private String id;
    private String userId;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private OrderType type;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String trackingNumber;
    private LocalDateTime estimatedDelivery;
    private LocalDateTime rentalStartDate;
    private LocalDateTime rentalEndDate;
    private List<OrderItemDTO> items;
}
