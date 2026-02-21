package com.camping.projet.service;

import com.camping.projet.dto.request.InterventionSecoursRequest;
import com.camping.projet.dto.response.InterventionSecoursResponse;
import com.camping.projet.enums.StatutIntervention;
import java.util.List;

public interface IInterventionSecoursService {
    InterventionSecoursResponse startIntervention(InterventionSecoursRequest request);

    InterventionSecoursResponse updateInterventionStatus(Long id, StatutIntervention statut);

    void completeIntervention(Long id, String rapport, List<String> materiel);

    InterventionSecoursResponse getInterventionById(Long id);

    List<InterventionSecoursResponse> getInterventionsByAlert(Long alerteId);

    List<InterventionSecoursResponse> getInterventionsByTeamMember(Long userId);
}
