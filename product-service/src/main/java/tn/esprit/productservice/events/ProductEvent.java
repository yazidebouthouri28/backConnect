package tn.esprit.productservice.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Event published to Kafka when product state changes.
 * Used by other microservices (Order Service, n8n) to react to product changes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEvent {
    private String eventType;       // CREATED, UPDATED, DELETED
    private Long productId;
    private String productName;
    private String sku;
    private BigDecimal price;
    private Integer stockQuantity;
    private Long categoryId;
    private Long sellerId;
    private Boolean isActive;
    private Map<String, Object> changes;  // For UPDATE events: field -> newValue
    private LocalDateTime timestamp;
}
