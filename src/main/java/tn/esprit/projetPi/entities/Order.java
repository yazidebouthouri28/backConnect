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
@Document(collection = "orders")
public class Order {

    @Id
    String id;

    String userId;
    LocalDateTime orderDate;

    OrderStatus status;
    OrderType type;

    BigDecimal totalAmount;

    String shippingAddress;
    String trackingNumber;
    LocalDateTime estimatedDelivery;

    LocalDateTime rentalStartDate;
    LocalDateTime rentalEndDate;
}
