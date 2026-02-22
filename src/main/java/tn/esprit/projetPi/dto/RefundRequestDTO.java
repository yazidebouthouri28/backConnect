package tn.esprit.projetPi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.projetPi.entities.RefundRequestType;
import tn.esprit.projetPi.entities.RefundStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefundRequestDTO {
    private String id;
    private String orderId;
    private String userId;
    private String sellerId;
    
    private RefundRequestType type;
    private RefundStatus status;
    
    private List<String> productIds;
    private List<Integer> quantities;
    
    private String reason;
    private String detailedDescription;
    private List<String> images;
    
    private BigDecimal requestedAmount;
    private BigDecimal approvedAmount;
    
    private String adminNotes;
    private String rejectionReason;
    
    private String trackingNumber;
    private Boolean itemsReceived;
    private LocalDateTime itemsReceivedAt;
    
    private String processedBy;
    private LocalDateTime processedAt;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
