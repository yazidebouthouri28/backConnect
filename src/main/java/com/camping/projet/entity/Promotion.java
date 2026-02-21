package com.camping.projet.entity;

import com.camping.projet.enums.CiblePromotion;
import com.camping.projet.enums.TypeReduction;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "promotions", uniqueConstraints = {
        @UniqueConstraint(name = "uc_promotion_code", columnNames = "codePromo")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Pattern(regexp = "^[A-Z0-9]+$")
    @Size(min = 3, max = 20)
    @Column(nullable = false, length = 20)
    private String codePromo;

    @Size(max = 200)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeReduction typeReduction;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false)
    private BigDecimal valeur;

    @NotNull
    private LocalDateTime dateDebut;

    @NotNull
    private LocalDateTime dateFin;

    @Min(1)
    private Integer maxUtilisations;

    private Integer nbUtilisationsActuelles;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CiblePromotion cible;

    private boolean actif;

    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ConditionPromotion> conditions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (nbUtilisationsActuelles == null) {
            nbUtilisationsActuelles = 0;
        }
    }

    // Business Methods
    public boolean isValideAujourdhui() {
        LocalDateTime now = LocalDateTime.now();
        return actif && !now.isBefore(dateDebut) && !now.isAfter(dateFin);
    }

    public boolean hasReachedLimit() {
        return maxUtilisations != null && nbUtilisationsActuelles >= maxUtilisations;
    }

    public void incrementUtilisations() {
        if (!hasReachedLimit()) {
            this.nbUtilisationsActuelles++;
        } else {
            throw new IllegalStateException("Promotion limit reached.");
        }
    }
}
