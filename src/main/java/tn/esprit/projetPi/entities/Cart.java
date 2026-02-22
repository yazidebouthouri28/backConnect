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
@Document(collection = "carts")
public class Cart {

    @Id
    String id;

    String userId;
    List<CartItem> items;
    BigDecimal totalAmount;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
