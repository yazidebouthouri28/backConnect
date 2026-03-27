package tn.esprit.productservice.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Event published to Kafka when product state changes.
 * Used by other microservices (Order Service, n8n) to react to product changes.
 * 
 * All IDs are String (UUID.toString()) for cross-service compatibility.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEvent {
    private String eventType;       // CREATED, UPDATED, DELETED
    private String productId;       // UUID as String
    private String productName;
    private String sku;
    private BigDecimal price;
    private Integer stockQuantity;
    private String categoryId;      // UUID as String
    private String sellerId;        // UUID as String
    private Boolean isActive;
    private Map<String, Object> changes;  // For UPDATE events: field -> newValue
    private LocalDateTime timestamp;
}
