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
@Document(collection = "refunds")
public class Refund {

    @Id
    String idRefund;

    String userId;
    String transactionId;
    Double amount;
    String reason;
    LocalDateTime refundDate;
    String status;
}
