package com.camping.projet.dto.request;

import com.camping.projet.enums.TypeExercice;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciceEvacuationRequest {
    @NotBlank
    @Size(max = 100)
    private String nom;

    @NotNull
    private TypeExercice type;

    @NotNull
    private LocalDateTime dateHeure;

    private Integer dureeMinutes;

    @NotNull
    @Min(1)
    private Integer dureeObjectifMinutes;

    @Size(max = 2000)
    private String pointsPositifs;

    @Size(max = 2000)
    private String pointsAmelioration;

    private LocalDateTime prochainExercice;

    @NotNull
    private Long responsableId;
}
