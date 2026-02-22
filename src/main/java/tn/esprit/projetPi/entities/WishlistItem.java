package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WishlistItem {
    String productId;
    String productName;
    String productImage;
    BigDecimal priceWhenAdded;
    BigDecimal currentPrice;
    Boolean inStock;
    LocalDateTime addedAt;
    String notes;
}
