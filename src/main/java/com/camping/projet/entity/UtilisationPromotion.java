package com.camping.projet.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "promotion_utilisations", indexes = {
        @Index(name = "idx_promo_util_user", columnList = "user_id"),
        @Index(name = "idx_promo_util_promo", columnList = "promotion_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilisationPromotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_promo_util_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = false, foreignKey = @ForeignKey(name = "fk_promo_util_promotion"))
    private Promotion promotion;

    @NotNull
    @Column(nullable = false)
    private Long reservationId; // FK to Reservation (Teammate's entity)

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montantAvantReduction;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montantReduction;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montantFinal;

    @Column(updatable = false)
    private LocalDateTime dateUtilisation;

    @PrePersist
    protected void onCreate() {
        dateUtilisation = LocalDateTime.now();
    }
}
