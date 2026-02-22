package tn.esprit.projetPi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.projetPi.entities.DiscountType;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponValidationResponse {
    private Boolean valid;
    private String message;
    private String couponCode;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal calculatedDiscount;
    private BigDecimal newTotal;
}
