package com.camping.projet.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "packs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal packPrice;

    @Column(precision = 12, scale = 2)
    private BigDecimal normalPrice;

    private BigDecimal savingsPercentage;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "pack_services", joinColumns = @JoinColumn(name = "pack_id"), inverseJoinColumns = @JoinColumn(name = "service_id"), foreignKey = @ForeignKey(name = "fk_pack_services_pack"), inverseForeignKey = @ForeignKey(name = "fk_pack_services_service"))
    @Builder.Default
    private Set<Service> services = new HashSet<>();

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private boolean active;

    @PrePersist
    @PreUpdate
    protected void calculateSavings() {
        if (services != null && !services.isEmpty()) {
            this.normalPrice = services.stream()
                    .map(Service::getPrice)
                    .filter(java.util.Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (this.normalPrice.compareTo(BigDecimal.ZERO) > 0 && this.packPrice != null) {
                BigDecimal savings = this.normalPrice.subtract(this.packPrice);
                this.savingsPercentage = savings.multiply(new BigDecimal("100"))
                        .divide(this.normalPrice, 2, RoundingMode.HALF_UP);
            } else {
                this.savingsPercentage = BigDecimal.ZERO;
            }
        }
    }

    // Business Methods
    public boolean isValidToday() {
        LocalDate now = LocalDate.now();
        return active && !now.isBefore(startDate) && !now.isAfter(endDate);
    }
}
