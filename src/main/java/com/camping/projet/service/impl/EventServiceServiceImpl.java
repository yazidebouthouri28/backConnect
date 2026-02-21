package com.camping.projet.service.impl;

import com.camping.projet.dto.request.EventServiceRequest;
import com.camping.projet.dto.response.EventServiceResponse;
import com.camping.projet.entity.EventService;
import com.camping.projet.entity.Service;
import com.camping.projet.mapper.EventServiceMapper;
import com.camping.projet.repository.EventServiceRepository;
import com.camping.projet.repository.ServiceRepository;
import com.camping.projet.service.IEventServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class EventServiceServiceImpl implements IEventServiceService {

    private final EventServiceRepository eventServiceRepository;
    private final ServiceRepository serviceRepository;
    private final EventServiceMapper eventServiceMapper;
    private final MessageSource messageSource;

    @Override
    @Transactional
    public EventServiceResponse assignServiceToEvent(EventServiceRequest request) {
        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException(
                        messageSource.getMessage("service.error.notfound", null, LocaleContextHolder.getLocale())));

        if (!service.isAvailable()) {
            throw new RuntimeException("Service is not available for assignment");
        }

        EventService eventService = eventServiceMapper.toEntity(request);
        eventService.setService(service);
        eventService.setAcceptedQuantity(0);

        return eventServiceMapper.toResponse(eventServiceRepository.save(eventService));
    }

    @Override
    @Transactional
    public void removeServiceFromEvent(Long eventServiceId) {
        eventServiceRepository.deleteById(eventServiceId);
    }

    @Override
    public List<EventServiceResponse> getServicesByEvent(Long eventId) {
        return eventServiceRepository.findByEventIdWithService(eventId).stream()
                .map(eventServiceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventServiceResponse> getAvailableEventServices() {
        return eventServiceRepository.findAllWithAvailableSpots().stream()
                .map(eventServiceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateRequiredQuantity(Long eventServiceId, Integer newQuantity) {
        EventService eventService = eventServiceRepository.findById(eventServiceId)
                .orElseThrow(() -> new RuntimeException(
                        messageSource.getMessage("event.service.notfound", null, LocaleContextHolder.getLocale())));
        eventService.setRequiredQuantity(newQuantity);
        eventServiceRepository.save(eventService);
    }
}
