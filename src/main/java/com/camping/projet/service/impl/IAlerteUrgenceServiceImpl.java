package com.camping.projet.service.impl;

import com.camping.projet.dto.request.AlerteUrgenceRequest;
import com.camping.projet.dto.response.AlerteUrgenceResponse;
import com.camping.projet.entity.AlerteUrgence;
import com.camping.projet.entity.ProtocoleUrgence;
import com.camping.projet.enums.StatutAlerte;
import com.camping.projet.mapper.AlerteUrgenceMapper;
import com.camping.projet.repository.AlerteUrgenceRepository;
import com.camping.projet.repository.ProtocoleUrgenceRepository;
import com.camping.projet.service.IAlerteUrgenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IAlerteUrgenceServiceImpl implements IAlerteUrgenceService {

    private final AlerteUrgenceRepository alerteRepository;
    private final ProtocoleUrgenceRepository protocoleRepository;
    private final AlerteUrgenceMapper alerteMapper;

    @Override
    @Transactional
    public AlerteUrgenceResponse triggerAlert(AlerteUrgenceRequest request) {
        ProtocoleUrgence protocole = protocoleRepository.findById(request.getProtocoleId())
                .orElseThrow(() -> new RuntimeException("Protocole not found"));

        AlerteUrgence alerte = alerteMapper.toEntity(request);
        alerte.setProtocole(protocole);
        alerte.setStatut(StatutAlerte.EN_COURS);
        alerte.setDateHeure(LocalDateTime.now());

        return alerteMapper.toResponse(alerteRepository.save(alerte));
    }

    @Override
    @Transactional
    public AlerteUrgenceResponse updateAlertStatus(Long id, StatutAlerte statut) {
        AlerteUrgence alerte = alerteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
        alerte.setStatut(statut);
        if (statut == StatutAlerte.RESOLUE) {
            alerte.setDateResolution(LocalDateTime.now());
        }
        return alerteMapper.toResponse(alerteRepository.save(alerte));
    }

    @Override
    @Transactional
    public AlerteUrgenceResponse updateAlertDetails(Long id, AlerteUrgenceRequest request) {
        AlerteUrgence alerte = alerteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
        alerteMapper.updateEntity(request, alerte);
        return alerteMapper.toResponse(alerteRepository.save(alerte));
    }

    @Override
    public AlerteUrgenceResponse getAlertById(Long id) {
        return alerteRepository.findById(id)
                .map(alerteMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
    }

    @Override
    public List<AlerteUrgenceResponse> getActiveAlerts() {
        return alerteRepository.findActiveAlerts().stream()
                .map(alerteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlerteUrgenceResponse> getAlertHistory() {
        return alerteRepository.findAll().stream()
                .map(alerteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void resolveAlert(Long id, String descriptionDegats, int nbBlesses) {
        AlerteUrgence alerte = alerteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
        alerte.setStatut(StatutAlerte.RESOLUE);
        alerte.setDateResolution(LocalDateTime.now());
        alerte.setDegatsMateriels(descriptionDegats);
        alerte.setNbBlesses(nbBlesses);
        alerteRepository.save(alerte);
    }
}
