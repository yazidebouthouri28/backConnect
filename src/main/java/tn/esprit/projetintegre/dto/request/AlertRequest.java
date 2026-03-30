package tn.esprit.projetintegre.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertRequest {
    @NotBlank(message = "Alert title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @NotBlank(message = "Alert type is required")
    private String alertType;

    @NotBlank(message = "Severity is required")
    private String severity;

    private Double latitude;
    private Double longitude;
    private String location;
    private Long siteId;
}
