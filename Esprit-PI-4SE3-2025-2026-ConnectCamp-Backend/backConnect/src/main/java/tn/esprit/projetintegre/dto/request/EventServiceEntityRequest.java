package tn.esprit.projetintegre.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import tn.esprit.projetintegre.enums.ServiceType;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventServiceEntityRequest {
    @NotBlank(message = "Le nom du service est obligatoire")
    private String name;

    private String description;

    @NotNull(message = "Le type de service est obligatoire")
    private ServiceType serviceType;

    @DecimalMin(value = "0.0", message = "Le prix doit être positif")
    private BigDecimal price;

    private Boolean included;
    private Boolean optional;

    @Min(value = 1, message = "La quantité requise doit être au moins 1")
    private Integer quantiteRequise;

    @NotNull(message = "L'ID de l'événement est obligatoire")
    private Long eventId;

    @NotNull(message = "L'ID du service est obligatoire")
    private Long serviceId;

    private Long providerId;
}
