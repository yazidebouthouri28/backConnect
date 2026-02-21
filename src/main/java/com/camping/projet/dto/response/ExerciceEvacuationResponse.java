package com.camping.projet.dto.response;

import com.camping.projet.enums.TypeExercice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciceEvacuationResponse {
    private Long id;
    private String nom;
    private TypeExercice type;
    private LocalDateTime dateHeure;
    private Integer dureeMinutes;
    private Integer dureeObjectifMinutes;
    private boolean reussi;
    private String pointsPositifs;
    private String pointsAmelioration;
    private LocalDateTime prochainExercice;
    private Long responsableId;
}
