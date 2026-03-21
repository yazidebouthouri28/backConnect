package tn.esprit.productservice.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Consumes order events from Kafka.
 * When an order is created, decrement product stock.
 * When an order is cancelled, restore product stock.
 *
 * TODO: Wire this to your ProductService/ProductRepository after migration.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    // TODO: Inject ProductRepository or ProductService here
    // private final ProductRepository productRepository;

    @KafkaListener(topics = "order.created", groupId = "product-service-group")
    public void handleOrderCreated(Map<String, Object> event) {
        log.info("Received order.created event: {}", event);
        // Extract order items and decrement stock
        // List<Map<String, Object>> items = (List<Map<String, Object>>) event.get("items");
        // for (Map<String, Object> item : items) {
        //     Long productId = ((Number) item.get("productId")).longValue();
        //     int quantity = ((Number) item.get("quantity")).intValue();
        //     productRepository.decrementStock(productId, quantity);
        // }
    }

    @KafkaListener(topics = "order.cancelled", groupId = "product-service-group")
    public void handleOrderCancelled(Map<String, Object> event) {
        log.info("Received order.cancelled event: {}", event);
        // Restore stock for cancelled order items
    }
}
