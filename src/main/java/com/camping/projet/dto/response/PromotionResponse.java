package com.camping.projet.dto.response;

import com.camping.projet.enums.CiblePromotion;
import com.camping.projet.enums.TypeReduction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionResponse {
    private Long id;
    private String codePromo;
    private String description;
    private TypeReduction typeReduction;
    private BigDecimal valeur;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private Integer maxUtilisations;
    private Integer nbUtilisationsActuelles;
    private CiblePromotion cible;
    private boolean actif;
    private List<ConditionPromotionResponse> conditions;
}
