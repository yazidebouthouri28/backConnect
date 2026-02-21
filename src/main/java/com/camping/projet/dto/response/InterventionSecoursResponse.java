package com.camping.projet.dto.response;

import com.camping.projet.enums.StatutIntervention;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterventionSecoursResponse {
    private Long id;
    private Long alerteId;
    private List<Long> membresEquipeIds;
    private LocalDateTime heureDepart;
    private LocalDateTime heureArrivee;
    private LocalDateTime heureFin;
    private StatutIntervention statut;
    private List<String> materielUtilise;
    private String rapportComplet;
}
