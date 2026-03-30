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
import java.util.Optional;
import java.util.UUID;

/**
 * Kafka consumer for Order Service events.
 *
 * FIX 1: Uses findByIdWithCategory() instead of findById() so the category
 * proxy is always initialized — avoids "no Session" if any code downstream
 * touches product.getCategory().
 *
 * FIX 2: Replaced ifPresent() lambda with explicit Optional.get() after
 * isPresent() check. The old lambda swallowed exceptions silently — any
 * RuntimeException thrown inside ifPresent() was caught by the outer
 * try/catch and logged, but the stock was never updated and no retry happened.
 * With explicit flow, exceptions propagate correctly to the @KafkaListener
 * retry mechanism.
 *
 * FIX 3: publishLowStock() is called inside the same try/catch as the rest
 * of the processing but via ProductEventPublisher which already swallows
 * Kafka exceptions internally — so low-stock alerts never kill the listener.
 *
 * FIX 4: @Transactional on the listener methods ensures all productRepository
 * .save() calls for multiple items in one order are committed atomically.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ProductRepository productRepository;
    private final ProductEventPublisher eventPublisher;

    @KafkaListener(topics = "order.created", groupId = "product-service-group")
    @Transactional
    @SuppressWarnings("unchecked")
    public void handleOrderCreated(Map<String, Object> event) {
        log.info("Received order.created event: orderId={}", event.get("orderId"));
        try {
            List<Map<String, Object>> items = (List<Map<String, Object>>) event.get("items");
            if (items == null || items.isEmpty()) {
                log.warn("order.created event has no items, orderId={}", event.get("orderId"));
                return;
            }

            for (Map<String, Object> item : items) {
                processOrderItem(item, false);
            }

        } catch (Exception e) {
            log.error("Error processing order.created event orderId={}: {}",
                    event.get("orderId"), e.getMessage(), e);
            // Re-throw so Kafka retry/DLQ mechanism can handle it
            throw new RuntimeException("Failed to process order.created event", e);
        }
    }

    @KafkaListener(topics = "order.cancelled", groupId = "product-service-group")
    @Transactional
    @SuppressWarnings("unchecked")
    public void handleOrderCancelled(Map<String, Object> event) {
        log.info("Received order.cancelled event: orderId={}", event.get("orderId"));
        try {
            List<Map<String, Object>> items = (List<Map<String, Object>>) event.get("items");
            if (items == null || items.isEmpty()) {
                log.warn("order.cancelled event has no items, orderId={}", event.get("orderId"));
                return;
            }

            for (Map<String, Object> item : items) {
                processOrderItem(item, true);
            }

        } catch (Exception e) {
            log.error("Error processing order.cancelled event orderId={}: {}",
                    event.get("orderId"), e.getMessage(), e);
            throw new RuntimeException("Failed to process order.cancelled event", e);
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * @param item      the order item map containing productId and quantity
     * @param restoring true = order cancelled (add stock back),
     *                  false = order created (deduct stock)
     */
    private void processOrderItem(Map<String, Object> item, boolean restoring) {
        String productIdStr = (String) item.get("productId");
        if (productIdStr == null) {
            log.warn("Order item missing productId, skipping");
            return;
        }

        int quantity = item.get("quantity") != null
                ? ((Number) item.get("quantity")).intValue() : 0;

        if (quantity <= 0) {
            log.warn("Order item has invalid quantity={} for productId={}, skipping",
                    quantity, productIdStr);
            return;
        }

        UUID productId;
        try {
            productId = UUID.fromString(productIdStr);
        } catch (IllegalArgumentException e) {
            log.error("Invalid productId format: {}, skipping", productIdStr);
            return;
        }

        // FIX: use findByIdWithCategory so category proxy is always initialized
        Optional<Product> productOpt = productRepository.findByIdWithCategory(productId);
        if (productOpt.isEmpty()) {
            log.error("Product not found for id={}, skipping stock update", productId);
            return;
        }

        Product product = productOpt.get();

        if (restoring) {
            // Order cancelled — restore stock
            int restoredStock = product.getStockQuantity() + quantity;
            product.setStockQuantity(restoredStock);
            product.setSalesCount(Math.max(0, product.getSalesCount() - quantity));
            productRepository.save(product);
            log.info("Stock restored for product '{}' ({}): +{} → new stock={}",
                    product.getName(), productId, quantity, restoredStock);

        } else {
            // Order created — deduct stock
            int previousStock = product.getStockQuantity();
            int newStock = Math.max(0, previousStock - quantity);
            product.setStockQuantity(newStock);
            product.setSalesCount(product.getSalesCount() + quantity);
            productRepository.save(product);
            log.info("Stock decremented for product '{}' ({}): {} → {}",
                    product.getName(), productId, previousStock, newStock);

            // Publish low-stock alert if needed
            // FIX: publishLowStock() is safe — ProductEventPublisher catches all
            // Kafka exceptions internally, so this never throws
            if (product.getMinStockLevel() != null && newStock <= product.getMinStockLevel()) {
                ProductEvent lowStockEvent = ProductEvent.builder()
                        .productId(product.getId().toString())
                        .productName(product.getName())
                        .sku(product.getSku())
                        .stockQuantity(newStock)
                        .sellerId(product.getSellerId() != null
                                ? product.getSellerId().toString() : null)
                        .build();
                eventPublisher.publishLowStock(lowStockEvent);
                log.warn("Low stock alert published for product '{}': stock={}",
                        product.getName(), newStock);
            }
        }
    }
}