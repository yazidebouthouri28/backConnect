package com.camping.projet.dto.request;

import com.camping.projet.enums.StatutAlerte;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlerteUrgenceRequest {
    @NotNull
    private Long protocoleId;

    private Double latitude;
    private Double longitude;

    @NotBlank
    @Size(max = 500)
    private String description;

    @NotNull
    private StatutAlerte statut;

    private int nbBlesses;
    private String degatsMateriels;

    @NotNull
    private Long declencheurId;
}
