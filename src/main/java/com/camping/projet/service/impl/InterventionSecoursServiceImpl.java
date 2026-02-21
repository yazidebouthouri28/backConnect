package com.camping.projet.service.impl;

import com.camping.projet.dto.request.InterventionSecoursRequest;
import com.camping.projet.dto.response.InterventionSecoursResponse;
import com.camping.projet.entity.AlerteUrgence;
import com.camping.projet.entity.InterventionSecours;
import com.camping.projet.enums.StatutIntervention;
import com.camping.projet.mapper.InterventionSecoursMapper;
import com.camping.projet.repository.AlerteUrgenceRepository;
import com.camping.projet.repository.InterventionSecoursRepository;
import com.camping.projet.service.IInterventionSecoursService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterventionSecoursServiceImpl implements IInterventionSecoursService {

    private final InterventionSecoursRepository interventionRepository;
    private final AlerteUrgenceRepository alerteRepository;
    private final InterventionSecoursMapper interventionMapper;

    @Override
    @Transactional
    public InterventionSecoursResponse startIntervention(InterventionSecoursRequest request) {
        AlerteUrgence alerte = alerteRepository.findById(request.getAlerteId())
                .orElseThrow(() -> new RuntimeException("Alert not found"));

        InterventionSecours intervention = interventionMapper.toEntity(request);
        intervention.setAlerte(alerte);
        intervention.setStatut(StatutIntervention.EN_ROUTE);
        intervention.setHeureDepart(LocalDateTime.now());

        return interventionMapper.toResponse(interventionRepository.save(intervention));
    }

    @Override
    @Transactional
    public InterventionSecoursResponse updateInterventionStatus(Long id, StatutIntervention statut) {
        InterventionSecours intervention = interventionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Intervention not found"));
        intervention.setStatut(statut);
        if (statut == StatutIntervention.TERMINEE) {
            intervention.setHeureFin(LocalDateTime.now());
        }
        return interventionMapper.toResponse(interventionRepository.save(intervention));
    }

    @Override
    @Transactional
    public void completeIntervention(Long id, String rapport, List<String> materiel) {
        InterventionSecours intervention = interventionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Intervention not found"));
        intervention.setStatut(StatutIntervention.TERMINEE);
        intervention.setHeureFin(LocalDateTime.now());
        intervention.setRapportComplet(rapport);
        intervention.setMaterielUtilise(materiel);
        interventionRepository.save(intervention);
    }

    @Override
    public InterventionSecoursResponse getInterventionById(Long id) {
        return interventionRepository.findById(id)
                .map(interventionMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Intervention not found"));
    }

    @Override
    public List<InterventionSecoursResponse> getInterventionsByAlert(Long alerteId) {
        return interventionRepository.findByAlerteId(alerteId).stream()
                .map(interventionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<InterventionSecoursResponse> getInterventionsByTeamMember(Long userId) {
        return interventionRepository.findByMembreEquipe(userId).stream()
                .map(interventionMapper::toResponse)
                .collect(Collectors.toList());
    }
}
