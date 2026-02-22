package tn.esprit.projetPi.dto;

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
public class CouponDTO {
    private String id;
    private String code;
    private String name;
    private String description;
    
    private DiscountType discountType;
    private BigDecimal discountValue;
    
    private BigDecimal minimumOrderAmount;
    private BigDecimal maximumDiscountAmount;
    
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    
    private Integer usageLimit;
    private Integer usageLimitPerUser;
    private Integer usedCount;
    
    private Boolean isActive;
    private Boolean isPublic;
    
    private List<String> applicableProductIds;
    private List<String> applicableCategoryIds;
    
    private Boolean firstOrderOnly;
    private Boolean newUsersOnly;
    
    private LocalDateTime createdAt;
}
