package com.camping.projet.dto.request;

import com.camping.projet.enums.Role;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {

    @NotBlank
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$")
    private String username;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @NotBlank
    @Size(min = 6, max = 50)
    private String password;

    @NotNull
    private Role role;

    @NotBlank
    @Size(min = 2, max = 50)
    private String nom;

    @NotBlank
    @Size(min = 2, max = 50)
    private String prenom;

    @NotBlank
    @Pattern(regexp = "^[0-9]{8,15}$")
    private String telephone;

    @Size(max = 200)
    private String adresse;

    @Size(max = 50)
    private String ville;

    @Size(max = 10)
    private String codePostal;

    @Size(max = 50)
    private String pays;

    @Past
    private LocalDate dateNaissance;

    @Size(max = 10)
    private String genre;

    private Long campingId;

    @Size(max = 20)
    private String numeroBadge;

    @Size(max = 500)
    private String competences;

    @Size(max = 10)
    private String langue;

    @Size(max = 10)
    private String devise;
}
