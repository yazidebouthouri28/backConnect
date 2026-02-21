package com.camping.projet.dto.response;

import com.camping.projet.enums.StatutAlerte;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlerteUrgenceResponse {
    private Long id;
    private ProtocoleUrgenceResponse protocole;
    private LocalDateTime dateHeure;
    private LocalDateTime dateResolution;
    private Double latitude;
    private Double longitude;
    private String description;
    private StatutAlerte statut;
    private Integer dureeMinutes;
    private int nbBlesses;
    private String degatsMateriels;
    private Long declencheurId;
}
