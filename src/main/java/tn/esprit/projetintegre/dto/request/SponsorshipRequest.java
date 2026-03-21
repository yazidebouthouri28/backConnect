// SponsorshipRequest.java
package tn.esprit.projetintegre.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SponsorshipRequest {
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    private String sponsorshipLevel; // Au lieu de tier
    private String benefits;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive; // Assurez-vous que ce champ existe
    // Ajoutez d'autres champs selon votre entité Sponsorship
}