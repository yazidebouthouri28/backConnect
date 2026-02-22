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
@Document(collection = "cart_items")
public class CartItem {

    @Id
    String id;

    String productId;
    String productName;

    BigDecimal price;
    Integer quantity;

    String image;

    OrderType type;
    Integer rentalDays;
}
