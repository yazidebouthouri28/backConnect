package tn.esprit.projetintegre.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VirtualTourRequest {
    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 3, max = 200, message = "Le titre doit contenir entre 3 et 200 caractères")
    private String title;

    @Size(max = 2000, message = "La description ne peut pas dépasser 2000 caractères")
    private String description;

    private String thumbnailUrl;
    private Integer durationMinutes;
    @Builder.Default
    private Boolean isFeatured = false;
    @NotNull(message = "Site ID is required")
    private Long siteId;
}
