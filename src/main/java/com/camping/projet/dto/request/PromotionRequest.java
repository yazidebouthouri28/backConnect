package com.camping.projet.dto.request;

import com.camping.projet.enums.CiblePromotion;
import com.camping.projet.enums.TypeReduction;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionRequest {

    @NotBlank
    @Pattern(regexp = "^[A-Z0-9]+$")
    @Size(min = 3, max = 20)
    private String codePromo;

    @Size(max = 200)
    private String description;

    @NotNull
    private TypeReduction typeReduction;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal valeur;

    @NotNull
    private LocalDateTime dateDebut;

    @NotNull
    private LocalDateTime dateFin;

    @Min(1)
    private Integer maxUtilisations;

    @NotNull
    private CiblePromotion cible;

    private boolean actif;

    private List<ConditionPromotionRequest> conditions;
}
