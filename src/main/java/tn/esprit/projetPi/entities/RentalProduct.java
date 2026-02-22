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
@Document(collection = "rental_products")
public class RentalProduct {

    @Id
    String id;

    BigDecimal rentalPrice;
    String rentalDuration;
    BigDecimal depositAmount;
    Integer maxRentalDays;
}
