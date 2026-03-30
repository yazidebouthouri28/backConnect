package tn.esprit.productservice.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import tn.esprit.productservice.config.KafkaConfig;

import java.util.concurrent.CompletableFuture;

/**
 * Publishes product events to Kafka topics.
 *
 * FIX: Every send is wrapped in try/catch so that:
 * - Synchronous failures (broker unreachable, max.block.ms timeout) are caught
 *   and logged without propagating up to the caller.
 * - Async failures (broker rejected after send) are caught in whenComplete().
 * - The caller (ProductService, OrderEventConsumer) NEVER gets an exception
 *   from Kafka — their DB work is already committed and should succeed.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishProductCreated(ProductEvent event) {
        event.setEventType("CREATED");
        send(KafkaConfig.TOPIC_PRODUCT_CREATED, event.getProductId(), event);
    }

    public void publishProductUpdated(ProductEvent event) {
        event.setEventType("UPDATED");
        send(KafkaConfig.TOPIC_PRODUCT_UPDATED, event.getProductId(), event);
    }

    public void publishProductDeleted(ProductEvent event) {
        event.setEventType("DELETED");
        send(KafkaConfig.TOPIC_PRODUCT_DELETED, event.getProductId(), event);
    }

    public void publishLowStock(ProductEvent event) {
        event.setEventType("LOW_STOCK");
        send(KafkaConfig.TOPIC_INVENTORY_LOW_STOCK, event.getProductId(), event);
    }

    // ── Private ───────────────────────────────────────────────────────────────

    private void send(String topic, String key, ProductEvent event) {
        try {
            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(topic, key, event);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Kafka async send failed [topic={}, eventType={}, productId={}]: {}",
                            topic, event.getEventType(), event.getProductId(), ex.getMessage());
                } else {
                    log.debug("Kafka event sent OK [topic={}, eventType={}, offset={}]",
                            topic, event.getEventType(),
                            result.getRecordMetadata().offset());
                }
            });

        } catch (Exception e) {
            // Catches: broker timeout (max.block.ms), serialization errors,
            // topic-not-found if auto-create disabled, etc.
            log.error("Kafka send failed synchronously [topic={}, eventType={}, productId={}]: {}",
                    topic, event.getEventType(), event.getProductId(), e.getMessage());
            // Never rethrow — Kafka being down must not fail the HTTP request
        }
    }
}