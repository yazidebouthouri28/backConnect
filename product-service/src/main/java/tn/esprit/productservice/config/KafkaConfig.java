package tn.esprit.productservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka topic configuration for Product Service.
 * Topics are also created via docker-compose kafka-init, but this ensures
 * they exist when running outside Docker.
 */
@Configuration
public class KafkaConfig {

    public static final String TOPIC_PRODUCT_CREATED = "product.created";
    public static final String TOPIC_PRODUCT_UPDATED = "product.updated";
    public static final String TOPIC_PRODUCT_DELETED = "product.deleted";
    public static final String TOPIC_INVENTORY_LOW_STOCK = "inventory.low-stock";

    @Bean
    public NewTopic productCreatedTopic() {
        return TopicBuilder.name(TOPIC_PRODUCT_CREATED)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic productUpdatedTopic() {
        return TopicBuilder.name(TOPIC_PRODUCT_UPDATED)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic productDeletedTopic() {
        return TopicBuilder.name(TOPIC_PRODUCT_DELETED)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic inventoryLowStockTopic() {
        return TopicBuilder.name(TOPIC_INVENTORY_LOW_STOCK)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
