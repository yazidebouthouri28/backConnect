package com.camping.projet.entity;

import com.camping.projet.enums.TypeCondition;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "promotion_conditions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConditionPromotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = false, foreignKey = @ForeignKey(name = "fk_condition_promotion"))
    private Promotion promotion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeCondition typeCondition;

    @NotBlank
    @Column(nullable = false)
    private String valeur; // Stored as String for flexibility (amount, days, date, etc.)

    @NotBlank
    @Size(max = 2)
    @Column(nullable = false, length = 2)
    private String operateur; // '>=', '>', '=', '<', '<='
}
