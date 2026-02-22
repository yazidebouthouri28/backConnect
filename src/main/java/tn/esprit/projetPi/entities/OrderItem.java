package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItem {

    String productId;
    String productName;
    String productSku;
    String sellerId;
    String sellerName;
    
    Integer quantity;
    BigDecimal unitPrice;
    BigDecimal totalPrice;
    
    // For rentals
    OrderType type;
    Integer rentalDays;
    BigDecimal rentalPricePerDay;
    
    String image;
    String variant;
    
    // Discount applied to this item
    BigDecimal discountAmount;
    BigDecimal finalPrice;
}
