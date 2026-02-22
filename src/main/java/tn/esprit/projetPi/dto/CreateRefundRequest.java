package tn.esprit.projetPi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.projetPi.entities.RefundRequestType;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRefundRequest {
    @NotBlank(message = "Order ID is required")
    private String orderId;
    
    @NotNull(message = "Refund type is required")
    private RefundRequestType type;
    
    @NotEmpty(message = "At least one product ID is required")
    private List<String> productIds;
    
    private List<Integer> quantities;
    
    @NotBlank(message = "Reason is required")
    private String reason;
    
    private String detailedDescription;
    private List<String> images;
    
    private BigDecimal requestedAmount;
}
