package tn.esprit.projetPi.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.projetPi.entities.DiscountType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCouponRequest {
    @NotBlank(message = "Coupon code is required")
    @Size(min = 3, max = 50, message = "Coupon code must be between 3 and 50 characters")
    private String code;
    
    @NotBlank(message = "Coupon name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Discount type is required")
    private DiscountType discountType;
    
    @NotNull(message = "Discount value is required")
    @Positive(message = "Discount value must be positive")
    private BigDecimal discountValue;
    
    @PositiveOrZero(message = "Minimum order amount cannot be negative")
    private BigDecimal minimumOrderAmount;
    
    @Positive(message = "Maximum discount amount must be positive")
    private BigDecimal maximumDiscountAmount;
    
    @NotNull(message = "Valid from date is required")
    private LocalDateTime validFrom;
    
    @NotNull(message = "Valid until date is required")
    @Future(message = "Valid until date must be in the future")
    private LocalDateTime validUntil;
    
    @Positive(message = "Usage limit must be positive")
    private Integer usageLimit;
    
    @Positive(message = "Usage limit per user must be positive")
    private Integer usageLimitPerUser;
    
    private Boolean isPublic;
    private List<String> applicableProductIds;
    private List<String> applicableCategoryIds;
    private List<String> excludedProductIds;
    private List<String> applicableUserIds;
    private Boolean firstOrderOnly;
    private Boolean newUsersOnly;
}
