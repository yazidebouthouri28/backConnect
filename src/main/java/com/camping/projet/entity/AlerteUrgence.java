package com.camping.projet.entity;

import com.camping.projet.enums.StatutAlerte;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "alertes_urgence", indexes = {
        @Index(name = "idx_alerte_statut", columnList = "statut"),
        @Index(name = "idx_alerte_date", columnList = "dateHeure")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlerteUrgence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protocole_id", nullable = false, foreignKey = @ForeignKey(name = "fk_alerte_protocole"))
    private ProtocoleUrgence protocole;

    @Column(nullable = false)
    private LocalDateTime dateHeure;

    private LocalDateTime dateResolution;

    private Double latitude;
    private Double longitude;

    @NotBlank
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutAlerte statut;

    private Integer dureeMinutes;

    @Min(0)
    private int nbBlesses;

    @Size(max = 1000)
    private String degatsMateriels;

    @OneToMany(mappedBy = "alerte", cascade = CascadeType.ALL)
    @Builder.Default
    private List<InterventionSecours> interventions = new ArrayList<>();

    @Column(nullable = false)
    private Long declencheurId; // User who triggered the alert

    @PrePersist
    protected void onCreate() {
        if (dateHeure == null) {
            dateHeure = LocalDateTime.now();
        }
        if (statut == null) {
            statut = StatutAlerte.EN_COURS;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (statut == StatutAlerte.RESOLUE && dateResolution == null) {
            dateResolution = LocalDateTime.now();
        }

        if (dateHeure != null && dateResolution != null) {
            this.dureeMinutes = (int) Duration.between(dateHeure, dateResolution).toMinutes();
        }
    }
}
