package tn.esprit.projetintegre.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import tn.esprit.projetintegre.enums.CriterieName;

import java.time.LocalDateTime;

@Entity
@Table(name = "certification_items", indexes = {
        @Index(name = "idx_cert_item_certification", columnList = "certification_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CertificationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Item name is required")
    @Size(max = 200)
    private String name;

    @Size(max = 1000)
    @Column(length = 1000)
    private String description;

    @Min(value = 0, message = "Score must be positive")
    @Max(value = 10, message = "Score cannot exceed 10")
    private Integer score;

    @Min(value = 0, message = "Required score must be positive")
    private Integer requiredScore;

    @Builder.Default
    private Boolean passed = false;

    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Criteria name is required")
    private CriterieName criteriaName;

    @Column(length = 1000)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_id", nullable = false)
    private Certification certification;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}