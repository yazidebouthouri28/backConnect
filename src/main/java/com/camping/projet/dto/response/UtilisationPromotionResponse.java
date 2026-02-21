package com.camping.projet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilisationPromotionResponse {
    private Long id;
    private UserResponse user;
    private Long promotionId;
    private Long reservationId;
    private BigDecimal montantAvantReduction;
    private BigDecimal montantReduction;
    private BigDecimal montantFinal;
    private LocalDateTime dateUtilisation;
}
