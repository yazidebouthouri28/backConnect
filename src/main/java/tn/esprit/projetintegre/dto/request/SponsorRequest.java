// SponsorRequest.java
package tn.esprit.projetintegre.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import tn.esprit.projetintegre.enums.SponsorTier;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SponsorRequest {
    @NotBlank(message = "Sponsor name is required")
    private String name;
    private String description;
    private String logo;
    private String website;
    private String email;
    private String phone;
    private String contactPerson;
    private SponsorTier tier;
    // Ajoutez d'autres champs selon votre entité Sponsor
}