package tn.esprit.productservice.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tn.esprit.productservice.config.KafkaConfig;

/**
 * Publishes product events to Kafka topics.
 * Integrate this into your existing ProductService when migrating.
 *
 * Example usage in ProductService:
 *   productEventPublisher.publishProductCreated(savedProduct);
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishProductCreated(ProductEvent event) {
        event.setEventType("CREATED");
        log.info("Publishing product.created event for productId={}", event.getProductId());
        kafkaTemplate.send(KafkaConfig.TOPIC_PRODUCT_CREATED, 
                String.valueOf(event.getProductId()), event);
    }

    public void publishProductUpdated(ProductEvent event) {
        event.setEventType("UPDATED");
        log.info("Publishing product.updated event for productId={}", event.getProductId());
        kafkaTemplate.send(KafkaConfig.TOPIC_PRODUCT_UPDATED, 
                String.valueOf(event.getProductId()), event);
    }

    public void publishProductDeleted(ProductEvent event) {
        event.setEventType("DELETED");
        log.info("Publishing product.deleted event for productId={}", event.getProductId());
        kafkaTemplate.send(KafkaConfig.TOPIC_PRODUCT_DELETED, 
                String.valueOf(event.getProductId()), event);
    }

    public void publishLowStock(ProductEvent event) {
        log.warn("Publishing inventory.low-stock alert for productId={}, stock={}", 
                event.getProductId(), event.getStockQuantity());
        kafkaTemplate.send(KafkaConfig.TOPIC_INVENTORY_LOW_STOCK, 
                String.valueOf(event.getProductId()), event);
    }
}
