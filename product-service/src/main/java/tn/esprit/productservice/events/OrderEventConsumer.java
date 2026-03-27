package tn.esprit.productservice.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.productservice.entities.Product;
import tn.esprit.productservice.repositories.ProductRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Kafka consumer that listens to Order Service events.
 * 
 * Consumed Events:
 * - order.created: Decrements product stock for each ordered item
 * - order.cancelled: Restores product stock for each cancelled item
 * 
 * This ensures inventory consistency across services via event-driven communication.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ProductRepository productRepository;
    private final ProductEventPublisher eventPublisher;

    /**
     * Handle order.created events - decrement stock for ordered products.
     */
    @KafkaListener(topics = "order.created", groupId = "product-service-group")
    @Transactional
    @SuppressWarnings("unchecked")
    public void handleOrderCreated(Map<String, Object> event) {
        log.info("Received order.created event: orderId={}", event.get("orderId"));
        try {
            List<Map<String, Object>> items = (List<Map<String, Object>>) event.get("items");
            if (items == null) return;

            for (Map<String, Object> item : items) {
                String productIdStr = (String) item.get("productId");
                Integer quantity = item.get("quantity") != null
                        ? ((Number) item.get("quantity")).intValue() : 0;

                if (productIdStr != null && quantity > 0) {
                    UUID productId = UUID.fromString(productIdStr);
                    productRepository.findById(productId).ifPresent(product -> {
                        int newStock = Math.max(0, product.getStockQuantity() - quantity);
                        product.setStockQuantity(newStock);
                        product.setSalesCount(product.getSalesCount() + quantity);
                        productRepository.save(product);

                        log.info("Stock decremented for product {}: {} -> {}",
                                product.getName(), newStock + quantity, newStock);

                        // Check for low stock and publish alert
                        if (newStock <= product.getMinStockLevel()) {
                            ProductEvent lowStockEvent = ProductEvent.builder()
                                    .productId(product.getId().toString())
                                    .productName(product.getName())
                                    .sku(product.getSku())
                                    .stockQuantity(newStock)
                                    .build();
                            eventPublisher.publishLowStock(lowStockEvent);
                        }
                    });
                }
            }
        } catch (Exception e) {
            log.error("Error processing order.created event: {}", e.getMessage(), e);
        }
    }

    /**
     * Handle order.cancelled events - restore stock for cancelled products.
     */
    @KafkaListener(topics = "order.cancelled", groupId = "product-service-group")
    @Transactional
    @SuppressWarnings("unchecked")
    public void handleOrderCancelled(Map<String, Object> event) {
        log.info("Received order.cancelled event: orderId={}", event.get("orderId"));
        try {
            List<Map<String, Object>> items = (List<Map<String, Object>>) event.get("items");
            if (items == null) return;

            for (Map<String, Object> item : items) {
                String productIdStr = (String) item.get("productId");
                Integer quantity = item.get("quantity") != null
                        ? ((Number) item.get("quantity")).intValue() : 0;

                if (productIdStr != null && quantity > 0) {
                    UUID productId = UUID.fromString(productIdStr);
                    productRepository.findById(productId).ifPresent(product -> {
                        product.setStockQuantity(product.getStockQuantity() + quantity);
                        product.setSalesCount(Math.max(0, product.getSalesCount() - quantity));
                        productRepository.save(product);
                        log.info("Stock restored for product {}: +{}", product.getName(), quantity);
                    });
                }
            }
        } catch (Exception e) {
            log.error("Error processing order.cancelled event: {}", e.getMessage(), e);
        }
    }
}
