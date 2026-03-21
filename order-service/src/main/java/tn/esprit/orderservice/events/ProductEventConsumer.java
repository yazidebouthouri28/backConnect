package tn.esprit.orderservice.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Consumes product events from Kafka.
 * Updates local product reference data when products change.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventConsumer {

    // TODO: Inject InventoryService or a local product cache after migration

    @KafkaListener(topics = "product.created", groupId = "order-service-group")
    public void handleProductCreated(Map<String, Object> event) {
        log.info("Received product.created event: {}", event);
        // Initialize inventory record for new product
        // inventoryService.initializeInventory(productId, stockQuantity);
    }

    @KafkaListener(topics = "product.updated", groupId = "order-service-group")
    public void handleProductUpdated(Map<String, Object> event) {
        log.info("Received product.updated event: {}", event);
        // Update local product price cache
        // redisTemplate.opsForValue().set("product:" + productId + ":price", newPrice);
    }

    @KafkaListener(topics = "product.deleted", groupId = "order-service-group")
    public void handleProductDeleted(Map<String, Object> event) {
        log.info("Received product.deleted event: {}", event);
        // Mark inventory as discontinued
    }
}
