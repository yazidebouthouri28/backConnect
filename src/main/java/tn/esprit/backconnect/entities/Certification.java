package tn.esprit.backconnect.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "certifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long certificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "siteId", nullable = false)
    private Site site;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CertificationStatus status;

    @Column(nullable = false)
    private Integer score; // 0-100

    @Column(nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @OneToMany(mappedBy = "certification", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CertificationItem> certificationItems = new ArrayList<>();
}