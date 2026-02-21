package com.camping.projet.service;

import com.camping.projet.dto.request.CandidatureServiceRequest;
import com.camping.projet.dto.response.CandidatureServiceResponse;
import com.camping.projet.enums.ApplicationStatus;
import java.util.List;

public interface ICandidatureService {
    CandidatureServiceResponse submitCandidature(CandidatureServiceRequest request);

    CandidatureServiceResponse updateCandidatureStatus(Long id, ApplicationStatus status);

    void withdrawCandidature(Long id);

    CandidatureServiceResponse getCandidatureById(Long id);

    List<CandidatureServiceResponse> getCandidaturesByUser(Long userId);

    List<CandidatureServiceResponse> getCandidaturesByEventService(Long eventServiceId);

    List<CandidatureServiceResponse> getCandidaturesByEvent(Long eventId);
}
