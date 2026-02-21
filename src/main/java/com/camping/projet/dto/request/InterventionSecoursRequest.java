package com.camping.projet.dto.request;

import com.camping.projet.enums.StatutIntervention;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterventionSecoursRequest {
    @NotNull
    private Long alerteId;

    private List<Long> membresEquipeIds;

    @NotNull
    private StatutIntervention statut;

    private List<String> materielUtilise;

    @Size(max = 5000)
    private String rapportComplet;
}
