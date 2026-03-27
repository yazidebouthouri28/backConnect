package tn.esprit.projetintegre.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    @NotNull(message = "Site ID is required")
    private Long siteId;

    @NotNull(message = "La note est obligatoire")
    @Min(value = 1, message = "La note doit être au moins 1")
    @Max(value = 5, message = "La note ne peut pas dépasser 5")
    private Integer rating;

    @Size(max = 100, message = "Le titre ne peut pas dépasser 100 caractères")
    private String title;

    @Size(max = 2000, message = "Le commentaire ne peut pas dépasser 2000 caractères")
    private String comment;

    private List<String> images;
    private Long userId;
}
