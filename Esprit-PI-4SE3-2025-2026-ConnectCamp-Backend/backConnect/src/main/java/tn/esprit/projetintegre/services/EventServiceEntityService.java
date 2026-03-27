package tn.esprit.projetintegre.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.projetintegre.entities.CampingService;
import tn.esprit.projetintegre.entities.Event;
import tn.esprit.projetintegre.entities.EventServiceEntity;
import tn.esprit.projetintegre.entities.User;
import tn.esprit.projetintegre.exception.BusinessException;
import tn.esprit.projetintegre.exception.ResourceNotFoundException;
import tn.esprit.projetintegre.repositories.CampingServiceRepository;
import tn.esprit.projetintegre.repositories.EventRepository;
import tn.esprit.projetintegre.repositories.EventServiceEntityRepository;
import tn.esprit.projetintegre.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceEntityService {

    private final EventServiceEntityRepository eventServiceEntityRepository;
    private final EventRepository eventRepository;
    private final CampingServiceRepository campingServiceRepository;
    private final UserRepository userRepository;

    public EventServiceEntity addServiceToEvent(Long eventId, Long serviceId, Integer quantiteRequise, String notes) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        CampingService service = campingServiceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceId));

        if (eventServiceEntityRepository.existsByEventIdAndServiceId(eventId, serviceId)) {
            throw new BusinessException("Ce service est déjà associé à cet événement");
        }

        EventServiceEntity eventService = EventServiceEntity.builder()
                .event(event)
                .service(service)
                .name(service.getName())
                .description(service.getDescription())
                .serviceType(service.getType())
                .price(service.getPrice())
                .quantiteRequise(quantiteRequise != null ? quantiteRequise : 1)
                .notes(notes)
                .provider(service.getProvider())
                .included(false)
                .optional(true)
                .quantiteAcceptee(0)
                .quantity(1)
                .build();

        return eventServiceEntityRepository.save(eventService);
    }

    public List<EventServiceEntity> getServicesByEvent(Long eventId) {
        return eventServiceEntityRepository.findByEventId(eventId);
    }

    public void removeServiceFromEvent(Long eventServiceId) {
        EventServiceEntity eventService = eventServiceEntityRepository.findById(eventServiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Event service link not found"));

        if (eventService.getQuantiteAcceptee() > 0) {
            throw new BusinessException("Impossible de supprimer ce service car des travailleurs y sont déjà affectés");
        }

        eventServiceEntityRepository.delete(eventService);
    }
}
