package tn.esprit.projetPi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.projetPi.entities.OrderType;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    private String productId;
    private String productName;
    private String productSku;
    private String sellerId;
    private String sellerName;
    
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    
    private OrderType type;
    private Integer rentalDays;
    private BigDecimal rentalPricePerDay;
    
    private String image;
    private String variant;
    
    private BigDecimal discountAmount;
    private BigDecimal finalPrice;
}
