package com.camping.projet.dto.response;

import com.camping.projet.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private boolean actif;
    private boolean emailVerified;
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
    private String langue;
    private String devise;
    private Integer nbReservations;
    private Integer nbCommandes;
    private BigDecimal totalDepense;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private LocalDateTime lastLogin;
}
