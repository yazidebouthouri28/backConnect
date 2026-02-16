package tn.esprit.backconnect.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "certification_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long certificationItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_id", nullable = false)
    private Certification certification;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CriterieName criteriaName;

    @Column(nullable = false)
    private Integer score; // 0-10

    @Column(columnDefinition = "TEXT")
    private String comment;
}