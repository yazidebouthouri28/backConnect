package tn.esprit.orderservice.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import tn.esprit.orderservice.dto.ProductDTO;

import java.util.UUID;

/**
 * Client service for inter-service communication with Product Service.
 * Uses WebClient to make synchronous REST calls to fetch product data.
 */
@Slf4j
@Service
public class ProductServiceClient {

    private final WebClient webClient;

    public ProductServiceClient(@Qualifier("productServiceWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Fetch product details from Product Service by ID.
     * Returns null if product not found or service unavailable.
     */
    public ProductDTO getProduct(UUID productId) {
        try {
            // The Product Service returns ApiResponse<ProductResponse>
            // We extract from the nested 'data' field
            var response = webClient.get()
                    .uri("/api/products/{id}", productId)
                    .retrieve()
                    .bodyToMono(ProductApiResponse.class)
                    .block();

            if (response != null && response.isSuccess() && response.getData() != null) {
                return response.getData();
            }
            log.warn("Product not found from Product Service: {}", productId);
            return null;
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Product not found: {}", productId);
            return null;
        } catch (Exception e) {
            log.error("Error fetching product from Product Service: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Inner class to deserialize ApiResponse<ProductDTO> from Product Service.
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    static class ProductApiResponse {
        private boolean success;
        private String message;
        private ProductDTO data;
    }
}
