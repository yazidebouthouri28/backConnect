package com.camping.projet.entity;

import com.camping.projet.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean actif;
    private boolean emailVerified;

    private String resetToken;
    private LocalDateTime resetTokenExpiry;
    private String verificationToken;
    private LocalDateTime verificationTokenExpiry;

    private String nom;
    private String prenom;
    private String telephone;
    private String adresse;
    private String ville;
    private String codePostal;
    private String pays;
    private LocalDate dateNaissance;
    private String genre;
    private String photoUrl;

    private Long campingId;
    private String numeroBadge;
    private String competences;

    // 3. GEOLOCATION (Urgence & Tracker)
    private Double latitude;
    private Double longitude;

    // 4. PROVIDER / SELLER INFO
    private String nomEntreprise;
    private String numPatente;

    // 5. CHAT / STATUS
    private boolean statusEnLigne;
    private LocalDateTime lastSeen;

    private boolean notificationsEmail;
    private boolean notificationsSms;
    private boolean notificationsPush;

    private String langue;
    private String devise;

    private Integer nbReservations;
    private Integer nbCommandes;
    private BigDecimal totalDepense;

    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private LocalDateTime lastLogin;
    private String ipAddress;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
        actif = true;
        emailVerified = false;
        nbReservations = 0;
        nbCommandes = 0;
        totalDepense = BigDecimal.ZERO;
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }

    // Business Methods
    public String getFullName() {
        return (prenom != null ? prenom : "") + " " + (nom != null ? nom : "");
    }

    public boolean isCampingStaff() {
        return campingId != null
                && (role == Role.ROLE_AGENT_SECURITE || role == Role.ROLE_ORGANISATEUR || role == Role.ROLE_PROVIDER);
    }

    public void incrementReservations() {
        if (this.nbReservations == null)
            this.nbReservations = 0;
        this.nbReservations++;
    }

    public void incrementCommandes() {
        if (this.nbCommandes == null)
            this.nbCommandes = 0;
        this.nbCommandes++;
    }

    public void addDepense(BigDecimal montant) {
        if (this.totalDepense == null)
            this.totalDepense = BigDecimal.ZERO;
        this.totalDepense = this.totalDepense.add(montant);
    }

    public void updateLastLogin(String ipAddress) {
        this.lastLogin = LocalDateTime.now();
        this.ipAddress = ipAddress;
    }
}
