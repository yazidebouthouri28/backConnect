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
@Document(collection = "transactions")
public class Transaction {

    @Id
    String id;

    String userId;
    String orderId;
    String paymentMethodId;
    String walletId;
    
    BigDecimal amount;
    BigDecimal fee;
    BigDecimal netAmount;
    
    TransactionType type;
    TransactionStatus status;
    
    String currency;
    String description;
    String reference;
    
    LocalDateTime transactionDate;
    LocalDateTime processedAt;
    
    // For refunds
    String originalTransactionId;
    String refundReason;
}
