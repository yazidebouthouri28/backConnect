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
@Document(collection = "refund_requests")
public class RefundRequest {

    @Id
    String id;

    String orderId;
    String userId;
    String sellerId;
    
    RefundRequestType type; // RETURN, REFUND, EXCHANGE
    RefundStatus status;
    
    List<String> productIds;
    List<Integer> quantities;
    
    String reason;
    String detailedDescription;
    List<String> images;
    
    BigDecimal requestedAmount;
    BigDecimal approvedAmount;
    
    String adminNotes;
    String rejectionReason;
    
    String trackingNumber; // For return shipment
    Boolean itemsReceived;
    LocalDateTime itemsReceivedAt;
    
    String processedBy;
    LocalDateTime processedAt;
    
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
