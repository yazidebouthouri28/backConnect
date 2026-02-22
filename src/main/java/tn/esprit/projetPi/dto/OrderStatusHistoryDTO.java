package tn.esprit.projetPi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.projetPi.entities.OrderStatus;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusHistoryDTO {
    private OrderStatus status;
    private LocalDateTime timestamp;
    private String updatedBy;
    private String notes;
    private String location;
}
