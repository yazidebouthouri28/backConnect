# Kafka Producer Configuration

## Spring Boot Configuration (already in application.yml)

```yaml
spring:
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      acks: all          # Wait for all replicas to acknowledge
      retries: 3         # Retry on transient failures
      properties:
        spring.json.add.type.headers: false   # Don't add Java type info (cross-service compatibility)
        max.block.ms: 5000                    # Max time to block on send
        linger.ms: 10                         # Batch small messages
        batch.size: 16384                     # 16KB batch size
        enable.idempotence: true              # Exactly-once semantics
```

## Usage Pattern

```java
// In your service class:
@Autowired
private ProductEventPublisher eventPublisher;

public ProductResponse createProduct(ProductRequest request) {
    Product product = /* save to DB */;

    // Publish event AFTER successful DB save
    eventPublisher.publishProductCreated(ProductEvent.builder()
        .productId(product.getId())
        .productName(product.getName())
        .price(product.getPrice())
        .stockQuantity(product.getStockQuantity())
        .categoryId(product.getCategory().getId())
        .sellerId(product.getSeller().getId())
        .timestamp(LocalDateTime.now())
        .build());

    return mapper.toResponse(product);
}
```

## Topic Naming Convention

| Pattern | Example | Description |
|---------|---------|-------------|
| `<domain>.<action>` | `product.created` | Standard event |
| `<domain>.<sub-action>` | `inventory.low-stock` | Sub-domain event |

## Error Handling

Configure a Dead Letter Topic (DLT) for failed messages:

```java
@Bean
public DefaultErrorHandler errorHandler() {
    return new DefaultErrorHandler(
        new DeadLetterPublishingRecoverer(kafkaTemplate),
        new FixedBackOff(1000L, 3)  // 3 retries, 1s apart
    );
}
```
