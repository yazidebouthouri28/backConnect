// SponsorRequest.java
package tn.esprit.backconnect.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

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
    // Ajoutez d'autres champs selon votre entité Sponsor
}