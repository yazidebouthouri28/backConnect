package com.camping.projet.entity;

import com.camping.projet.enums.TypeExercice;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exercices_evacuation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciceEvacuation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nom;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeExercice type;

    @Column(nullable = false)
    private LocalDateTime dateHeure;

    private Integer dureeMinutes;

    @NotNull
    @Min(1)
    private Integer dureeObjectifMinutes;

    private boolean reussi;

    @Size(max = 2000)
    @Column(length = 2000)
    private String pointsPositifs;

    @Size(max = 2000)
    @Column(length = 2000)
    private String pointsAmelioration;

    private LocalDateTime prochainExercice;

    private Long responsableId; // AGENT_SECURITE OR ADMIN

    @PrePersist
    @PreUpdate
    protected void checkSuccess() {
        if (dureeMinutes != null && dureeObjectifMinutes != null) {
            this.reussi = dureeMinutes <= dureeObjectifMinutes;
        }
    }
}
