package tn.esprit.projetPi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplyCouponRequest {
    @NotBlank(message = "Coupon code is required")
    private String code;
    
    private BigDecimal orderTotal;
    private List<String> productIds;
    private List<String> categoryIds;
}
