package com.camping.projet.service;

import com.camping.projet.dto.request.AlerteUrgenceRequest;
import com.camping.projet.dto.response.AlerteUrgenceResponse;
import com.camping.projet.enums.StatutAlerte;
import java.util.List;

public interface IAlerteUrgenceService {
    AlerteUrgenceResponse triggerAlert(AlerteUrgenceRequest request);

    AlerteUrgenceResponse updateAlertStatus(Long id, StatutAlerte statut);

    AlerteUrgenceResponse updateAlertDetails(Long id, AlerteUrgenceRequest request);

    AlerteUrgenceResponse getAlertById(Long id);

    List<AlerteUrgenceResponse> getActiveAlerts();

    List<AlerteUrgenceResponse> getAlertHistory();

    void resolveAlert(Long id, String descriptionDegats, int nbBlesses);
}
