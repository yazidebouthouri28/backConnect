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
@Document(collection = "transactions")
public class Transaction {

    @Id
    String idTransaction;

    String userId;
    String paymentMethodId;
    Double amount;
    LocalDateTime transactionDate;
    String status;
}
