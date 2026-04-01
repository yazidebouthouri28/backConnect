package tn.esprit.orderservice.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tn.esprit.orderservice.config.KafkaConfig;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderCreated(OrderEvent event) {
        event.setEventType("CREATED");
        log.info("Publishing order.created event for orderId={}", event.getOrderId());
        kafkaTemplate.send(KafkaConfig.TOPIC_ORDER_CREATED,
                        String.valueOf(event.getOrderId()), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send order.created event: {}", ex.getMessage());
                    } else {
                        log.info("order.created event sent successfully to topic={}", KafkaConfig.TOPIC_ORDER_CREATED);
                    }
                });
    }

    public void publishOrderCompleted(OrderEvent event) {
        event.setEventType("COMPLETED");
        log.info("Publishing order.completed event for orderId={}", event.getOrderId());
        kafkaTemplate.send(KafkaConfig.TOPIC_ORDER_COMPLETED,
                        String.valueOf(event.getOrderId()), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send order.completed event: {}", ex.getMessage());
                    }
                });
    }

    public void publishOrderCancelled(OrderEvent event) {
        event.setEventType("CANCELLED");
        log.info("Publishing order.cancelled event for orderId={}", event.getOrderId());
        kafkaTemplate.send(KafkaConfig.TOPIC_ORDER_CANCELLED,
                        String.valueOf(event.getOrderId()), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send order.cancelled event: {}", ex.getMessage());
                    }
                });
    }
}