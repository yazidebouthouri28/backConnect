package tn.esprit.orderservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    @NotBlank(message = "Shipping name is required")
    private String shippingName;
    private String shippingPhone;
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;
    @NotBlank(message = "Shipping city is required")
    private String shippingCity;
    @NotBlank(message = "Shipping postal code is required")
    private String shippingPostalCode;
    @NotBlank(message = "Shipping country is required")
    private String shippingCountry;
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
    private String notes;
    private String couponCode;
}
