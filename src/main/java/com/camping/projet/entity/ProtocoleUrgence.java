package com.camping.projet.entity;

import com.camping.projet.enums.TypeUrgence;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "protocoles_urgence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProtocoleUrgence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 100)
    @Column(nullable = false, length = 100)
    private String nom;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeUrgence type;

    @ElementCollection
    @CollectionTable(name = "protocole_etapes", joinColumns = @JoinColumn(name = "protocole_id"))
    @Column(name = "etape")
    @Builder.Default
    private List<String> etapes = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "protocole_contacts", joinColumns = @JoinColumn(name = "protocole_id"))
    @MapKeyColumn(name = "role_contact")
    @Column(name = "numero_contact")
    @Builder.Default
    private Map<String, String> contactsUrgence = new HashMap<>();

    @Min(1)
    @Max(5)
    private int gravite;

    private LocalDateTime derniereRevision;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        derniereRevision = LocalDateTime.now();
    }
}
