package tn.esprit.projetintegre.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import tn.esprit.projetintegre.enums.CertificationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "certifications", indexes = {
        @Index(name = "idx_certification_site", columnList = "site_id"),
        @Index(name = "idx_certification_status", columnList = "status"),
        @Index(name = "idx_certification_code", columnList = "certificationCode")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Certification code is required")
    @Size(max = 50)
    @Column(unique = true)
    private String certificationCode;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200)
    private String title;

    @Size(max = 2000)
    @Column(length = 2000)
    private String description;

    @NotBlank(message = "Issuing organization is required")
    @Size(max = 200)
    private String issuingOrganization;

    @NotNull(message = "Issue date is required")
    private LocalDate issueDate;

    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is required")
    @Builder.Default
    private CertificationStatus status = CertificationStatus.PENDING;

    private String documentUrl;
    private String verificationUrl;

    @Min(value = 0, message = "Score must be positive")
    @Max(value = 100, message = "Score cannot exceed 100")
    private Integer score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @OneToMany(mappedBy = "certification", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CertificationItem> certificationItems = new ArrayList<>();

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}