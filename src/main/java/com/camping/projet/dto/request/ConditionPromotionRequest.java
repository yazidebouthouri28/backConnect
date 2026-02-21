package com.camping.projet.dto.request;

import com.camping.projet.enums.TypeCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConditionPromotionRequest {
    @NotNull
    private Long promotionId;

    @NotNull
    private TypeCondition typeCondition;

    @NotBlank
    private String valeur;

    @NotBlank
    @Size(max = 2)
    private String operateur;
}
