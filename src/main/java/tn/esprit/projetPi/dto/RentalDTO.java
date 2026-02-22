package tn.esprit.projetPi.dto;

import lombok.Data;
import tn.esprit.projetPi.entities.RentalStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RentalDTO {
    private String id;
    private String productId;
    private String productName;
    private String userId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer daysLeft;
    private RentalStatus status;
    private BigDecimal totalCost;
    private BigDecimal depositAmount;
    private Boolean depositReturned;
}
