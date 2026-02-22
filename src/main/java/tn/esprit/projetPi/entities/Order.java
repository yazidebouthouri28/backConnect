package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "orders")
public class Order {

    @Id
    String id;

    // User info (buyer)
    String userId;
    String buyerName;
    String buyerEmail;
    
    // Seller info (for marketplace orders)
    String sellerId;
    String sellerName;
    
    LocalDateTime orderDate;
    OrderStatus status;
    OrderType type;

    // Order items stored directly in order
    List<OrderItem> items = new ArrayList<>();
    
    // Pricing
    BigDecimal subtotal;
    BigDecimal discountAmount;
    BigDecimal shippingCost;
    BigDecimal taxAmount;
    BigDecimal totalAmount;
    
    // Coupon
    String couponCode;
    String couponId;

    // Shipping
    ShippingAddress shippingAddress;
    ShippingAddress billingAddress;
    String trackingNumber;
    String shippingCarrier;
    LocalDateTime estimatedDelivery;
    LocalDateTime actualDelivery;

    // Rental specific
    LocalDateTime rentalStartDate;
    LocalDateTime rentalEndDate;
    BigDecimal rentalDeposit;
    
    // Order history
    List<OrderStatusHistory> statusHistory = new ArrayList<>();
    
    // Payment
    String transactionId;
    PaymentMethod paymentMethod;
    Boolean isPaid;
    LocalDateTime paidAt;
    
    // Notes
    String customerNotes;
    String internalNotes;
    
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
