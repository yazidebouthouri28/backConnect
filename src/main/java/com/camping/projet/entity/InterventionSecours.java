package com.camping.projet.entity;

import com.camping.projet.enums.StatutIntervention;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "interventions_secours", indexes = {
        @Index(name = "idx_intervention_statut", columnList = "statut")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterventionSecours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alerte_id", nullable = false, foreignKey = @ForeignKey(name = "fk_intervention_alerte"))
    private AlerteUrgence alerte;

    @ElementCollection
    @CollectionTable(name = "intervention_membres", joinColumns = @JoinColumn(name = "intervention_id"))
    @Column(name = "user_id")
    @Builder.Default
    private List<Long> membresEquipeIds = new ArrayList<>();

    private LocalDateTime heureDepart;
    private LocalDateTime heureArrivee;
    private LocalDateTime heureFin;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutIntervention statut;

    @ElementCollection
    @CollectionTable(name = "intervention_materiel", joinColumns = @JoinColumn(name = "intervention_id"))
    @Column(name = "materiel")
    @Builder.Default
    private List<String> materielUtilise = new ArrayList<>();

    @Size(max = 5000)
    @Column(length = 5000)
    private String rapportComplet;

    @PrePersist
    protected void onCreate() {
        if (statut == null) {
            statut = StatutIntervention.EN_ROUTE;
        }
        if (heureDepart == null) {
            heureDepart = LocalDateTime.now();
        }
    }
}
