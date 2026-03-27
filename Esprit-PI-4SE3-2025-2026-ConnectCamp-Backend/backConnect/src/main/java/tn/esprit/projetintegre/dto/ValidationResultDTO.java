package tn.esprit.projetintegre.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResultDTO {
    private boolean valide;
    private BigDecimal montantFinal;
    private BigDecimal reduction;
    private String message;
}
