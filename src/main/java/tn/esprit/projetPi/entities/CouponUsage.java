package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "coupon_usages")
public class CouponUsage {

    @Id
    String id;

    String couponId;
    String couponCode;
    String userId;
    String orderId;
    BigDecimal discountApplied;
    LocalDateTime usedAt;
}
