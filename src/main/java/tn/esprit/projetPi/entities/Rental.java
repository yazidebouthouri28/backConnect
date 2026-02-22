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
@Document(collection = "rentals")
public class Rental {

    @Id
    String id;

    String productId;
    String productName;
    String userId;

    LocalDateTime startDate;
    LocalDateTime endDate;

    Integer daysLeft;

    RentalStatus status;

    BigDecimal totalCost;
    BigDecimal depositAmount;

    Boolean depositReturned;
}
