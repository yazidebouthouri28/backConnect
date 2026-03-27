package tn.esprit.orderservice.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Kafka consumer that listens to Product Service events.
 * 
 * Consumed Events:
 * - product.created: Log new product availability
 * - product.updated: Update local product reference cache (price changes, etc.)
 * - product.deleted: Mark product as unavailable in local cache
 * 
 * In this microservice, product data is denormalized in CartItem and OrderItem,
 * so these events are primarily for cache invalidation and logging.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventConsumer {

    /**
     * Handle product.created events - log new product availability.
     */
    @KafkaListener(topics = "product.created", groupId = "order-service-group")
    public void handleProductCreated(Map<String, Object> event) {
        log.info("Received product.created event: productId={}, productName={}",
                event.get("productId"), event.get("productName"));
        // Product data is fetched on-demand via WebClient when adding to cart,
        // so no local cache update needed for creation events.
    }

    /**
     * Handle product.updated events - log price/stock changes.
     * CartItem prices are snapshot at add-time, but this could trigger
     * a notification to users with this product in their cart.
     */
    @KafkaListener(topics = "product.updated", groupId = "order-service-group")
    public void handleProductUpdated(Map<String, Object> event) {
        log.info("Received product.updated event: productId={}, changes={}",
                event.get("productId"), event.get("changes"));
        // Future enhancement: notify users with this product in their cart
        // about price changes or stock issues.
    }

    /**
     * Handle product.deleted events - product is no longer available.
     */
    @KafkaListener(topics = "product.deleted", groupId = "order-service-group")
    public void handleProductDeleted(Map<String, Object> event) {
        log.info("Received product.deleted event: productId={}, productName={}",
                event.get("productId"), event.get("productName"));
        // Future enhancement: remove this product from active carts
        // and notify affected users.
    }
}
