package tn.esprit.orderservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient configuration for inter-service communication.
 * Order Service calls Product Service to validate stock and get product info.
 */
@Configuration
public class WebClientConfig {

    @Value("${services.product-service.url:http://localhost:8082}")
    private String productServiceUrl;

    @Bean
    public WebClient productServiceWebClient() {
        return WebClient.builder()
                .baseUrl(productServiceUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
