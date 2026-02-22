package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "order_items")
public class OrderItem {

    @Id
    String id;

    String productId;
    String productName;

    Integer quantity;
    BigDecimal price;

    OrderType type;
    Integer rentalDays;

    String image;
}
