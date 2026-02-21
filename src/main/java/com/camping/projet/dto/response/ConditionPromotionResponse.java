package com.camping.projet.dto.response;

import com.camping.projet.enums.TypeCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConditionPromotionResponse {
    private Long id;
    private Long promotionId;
    private TypeCondition typeCondition;
    private String valeur;
    private String operateur;
}
