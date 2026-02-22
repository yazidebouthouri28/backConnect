package tn.esprit.projetPi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.projetPi.entities.OrderStatus;
import tn.esprit.projetPi.entities.OrderType;
import tn.esprit.projetPi.entities.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private String id;
    private String userId;
    private String buyerName;
    private String buyerEmail;
    private String sellerId;
    private String sellerName;
    
    private LocalDateTime orderDate;
    private OrderStatus status;
    private OrderType type;
    
    private List<OrderItemDTO> items;
    
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal shippingCost;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    
    private String couponCode;
    
    private ShippingAddressDTO shippingAddress;
    private ShippingAddressDTO billingAddress;
    private String trackingNumber;
    private String shippingCarrier;
    private LocalDateTime estimatedDelivery;
    private LocalDateTime actualDelivery;
    
    private LocalDateTime rentalStartDate;
    private LocalDateTime rentalEndDate;
    private BigDecimal rentalDeposit;
    
    private List<OrderStatusHistoryDTO> statusHistory;
    
    private PaymentMethod paymentMethod;
    private Boolean isPaid;
    private LocalDateTime paidAt;
    
    private String customerNotes;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
