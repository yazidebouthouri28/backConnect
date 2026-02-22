package tn.esprit.projetPi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.projetPi.entities.TransactionStatus;
import tn.esprit.projetPi.entities.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private String id;
    private String userId;
    private String orderId;
    private String walletId;
    
    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal netAmount;
    
    private TransactionType type;
    private TransactionStatus status;
    
    private String currency;
    private String description;
    private String reference;
    
    private LocalDateTime transactionDate;
    private LocalDateTime processedAt;
    
    private String originalTransactionId;
    private String refundReason;
}
