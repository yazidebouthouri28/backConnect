package com.camping.projet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationResultDTO {
    private boolean valid;
    private BigDecimal montantFinal;
    private BigDecimal reduction;
    private String message;
}
