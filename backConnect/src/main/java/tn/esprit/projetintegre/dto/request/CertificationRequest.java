package tn.esprit.projetintegre.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import tn.esprit.projetintegre.enums.CertificationStatus;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificationRequest {
    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 3, max = 200, message = "Le titre doit contenir entre 3 et 200 caractères")
    private String title;

    @Size(max = 2000, message = "La description ne peut pas dépasser 2000 caractères")
    private String description;

    @NotBlank(message = "L'organisme est obligatoire")
    private String issuingOrganization;

    @NotNull(message = "La date d'émission est obligatoire")
    private LocalDate issueDate;

    private LocalDate expirationDate;
    private String documentUrl;
    private String verificationUrl;
    @Min(value = 0, message = "Score must be positive")
    @Max(value = 100, message = "Score cannot exceed 100")
    private Integer score;
    @NotNull(message = "Site ID is required")
    private Long siteId;
}
