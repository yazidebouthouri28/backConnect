# Kafka Consumer Configuration

## Spring Boot Configuration (already in application.yml)

```yaml
spring:
  kafka:
    consumer:
      group-id: ${spring.application.name}-group   # e.g., product-service-group
      auto-offset-reset: earliest                   # Read from beginning if no offset
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"           # Trust all packages for deserialization
        max.poll.records: 100                       # Max records per poll
        session.timeout.ms: 30000                   # Consumer session timeout
        heartbeat.interval.ms: 10000                # Heartbeat interval
```

## Consumer Pattern

```java
@KafkaListener(topics = "order.created", groupId = "product-service-group")
public void handleOrderCreated(Map<String, Object> event) {
    log.info("Processing order.created: orderId={}", event.get("orderId"));

    List<Map<String, Object>> items = (List<Map<String, Object>>) event.get("items");
    for (Map<String, Object> item : items) {
        Long productId = ((Number) item.get("productId")).longValue();
        int quantity = ((Number) item.get("quantity")).intValue();

        productRepository.decrementStock(productId, quantity);

        // Check low stock threshold
        Product product = productRepository.findById(productId).orElseThrow();
        if (product.getStockQuantity() < 10) {
            eventPublisher.publishLowStock(...);
        }
    }
}
```

## Consumer Groups

| Service | Group ID | Subscribed Topics |
|---------|----------|-------------------|
| Product Service | `product-service-group` | `order.created`, `order.cancelled` |
| Order Service | `order-service-group` | `product.created`, `product.updated`, `product.deleted` |
| n8n | (webhook-based) | `order.created`, `order.completed`, `inventory.low-stock` |

## Idempotency

Always design consumers to be idempotent (safe to replay):
1. Use event IDs to deduplicate
2. Use conditional updates (`UPDATE ... WHERE stock >= :quantity`)
3. Store processed event IDs in a local table
