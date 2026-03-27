package tn.esprit.orderservice.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Event published to Kafka when order state changes.
 * All IDs are String (UUID.toString()) for cross-service compatibility.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private String eventType;       // CREATED, COMPLETED, CANCELLED
    private String orderId;         // UUID as String
    private String orderNumber;
    private String userId;          // UUID as String
    private String userEmail;
    private BigDecimal totalAmount;
    private String status;
    private String paymentStatus;
    private List<OrderItemEvent> items;
    private LocalDateTime timestamp;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemEvent {
        private String productId;   // UUID as String
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
    }
}
