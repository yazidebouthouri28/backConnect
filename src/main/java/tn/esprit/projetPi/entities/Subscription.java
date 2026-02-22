package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "subscriptions")
public class Subscription {

    @Id
    String idSubscription;

    String userId;
    String type;
    Double price;
    LocalDateTime startDate;
    LocalDateTime endDate;
    String status;
}
