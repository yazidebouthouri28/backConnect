package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "coupons")
public class Coupon {

    @Id
    String id;

    String code; // Unique coupon code
    String name;
    String description;
    
    DiscountType discountType; // PERCENTAGE, FIXED_AMOUNT, FREE_SHIPPING
    BigDecimal discountValue;
    
    BigDecimal minimumOrderAmount;
    BigDecimal maximumDiscountAmount;
    
    LocalDateTime validFrom;
    LocalDateTime validUntil;
    
    Integer usageLimit; // Total usage limit
    Integer usageLimitPerUser;
    Integer usedCount;
    
    Boolean isActive;
    Boolean isPublic; // Can be discovered by users
    
    // Restrictions
    List<String> applicableProductIds;
    List<String> applicableCategoryIds;
    List<String> excludedProductIds;
    List<String> applicableUserIds; // For user-specific coupons
    
    Boolean firstOrderOnly;
    Boolean newUsersOnly;
    
    String createdBy;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
