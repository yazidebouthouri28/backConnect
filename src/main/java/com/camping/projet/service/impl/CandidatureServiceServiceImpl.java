package com.camping.projet.service.impl;

import com.camping.projet.dto.request.CandidatureServiceRequest;
import com.camping.projet.dto.response.CandidatureServiceResponse;
import com.camping.projet.entity.CandidatureService;
import com.camping.projet.entity.EventService;
import com.camping.projet.entity.User;
import com.camping.projet.enums.ApplicationStatus;
import com.camping.projet.mapper.CandidatureServiceMapper;
import com.camping.projet.repository.CandidatureServiceRepository;
import com.camping.projet.repository.EventServiceRepository;
import com.camping.projet.repository.UserRepository;
import com.camping.projet.service.ICandidatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class CandidatureServiceServiceImpl implements ICandidatureService {

    private final CandidatureServiceRepository candidatureRepository;
    private final EventServiceRepository eventServiceRepository;
    private final UserRepository userRepository;
    private final CandidatureServiceMapper candidatureMapper;
    private final MessageSource messageSource;

    @Override
    @Transactional
    public CandidatureServiceResponse submitCandidature(CandidatureServiceRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException(
                        messageSource.getMessage("user.error.notfound", null, LocaleContextHolder.getLocale())));

        EventService eventService = eventServiceRepository.findById(request.getEventServiceId())
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage("event.service.requirement.notfound",
                        null, LocaleContextHolder.getLocale())));

        if (candidatureRepository.findByUserIdAndEventServiceId(request.getUserId(), request.getEventServiceId())
                .isPresent()) {
            throw new RuntimeException(messageSource.getMessage("candidature.error.already.applied", null,
                    LocaleContextHolder.getLocale()));
        }

        CandidatureService candidature = candidatureMapper.toEntity(request);
        candidature.setUser(user);
        candidature.setEventService(eventService);
        candidature.setStatus(ApplicationStatus.PENDING);
        candidature.setApplicationDate(LocalDateTime.now());

        return candidatureMapper.toResponse(candidatureRepository.save(candidature));
    }

    @Override
    @Transactional
    public CandidatureServiceResponse updateCandidatureStatus(Long id, ApplicationStatus status) {
        CandidatureService candidature = candidatureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        messageSource.getMessage("candidature.error.notfound", null, LocaleContextHolder.getLocale())));

        ApplicationStatus oldStatus = candidature.getStatus();
        candidature.setStatus(status);

        if (status == ApplicationStatus.ACCEPTED && oldStatus != ApplicationStatus.ACCEPTED) {
            EventService es = candidature.getEventService();
            es.setAcceptedQuantity(es.getAcceptedQuantity() + 1);
            eventServiceRepository.save(es);
        } else if (status != ApplicationStatus.ACCEPTED && oldStatus == ApplicationStatus.ACCEPTED) {
            EventService es = candidature.getEventService();
            es.setAcceptedQuantity(Math.max(0, es.getAcceptedQuantity() - 1));
            eventServiceRepository.save(es);
        }

        return candidatureMapper.toResponse(candidatureRepository.save(candidature));
    }

    @Override
    @Transactional
    public void withdrawCandidature(Long id) {
        CandidatureService candidature = candidatureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        messageSource.getMessage("candidature.error.notfound", null, LocaleContextHolder.getLocale())));

        if (candidature.getStatus() == ApplicationStatus.ACCEPTED) {
            EventService es = candidature.getEventService();
            es.setAcceptedQuantity(Math.max(0, es.getAcceptedQuantity() - 1));
            eventServiceRepository.save(es);
        }

        candidatureRepository.deleteById(id);
    }

    @Override
    public CandidatureServiceResponse getCandidatureById(Long id) {
        return candidatureRepository.findById(id)
                .map(candidatureMapper::toResponse)
                .orElseThrow(() -> new RuntimeException(
                        messageSource.getMessage("candidature.error.notfound", null, LocaleContextHolder.getLocale())));
    }

    @Override
    public List<CandidatureServiceResponse> getCandidaturesByUser(Long userId) {
        return candidatureRepository.findByUserId(userId).stream()
                .map(candidatureMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CandidatureServiceResponse> getCandidaturesByEventService(Long eventServiceId) {
        return candidatureRepository.findByEventServiceId(eventServiceId).stream()
                .map(candidatureMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CandidatureServiceResponse> getCandidaturesByEvent(Long eventId) {
        return candidatureRepository.findByEventId(eventId).stream()
                .map(candidatureMapper::toResponse)
                .collect(Collectors.toList());
    }
}
