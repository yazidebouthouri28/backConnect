package tn.esprit.projetintegre.services;

import lombok.RequiredArgsConstructor;
import tn.esprit.projetintegre.exception.AccessDeniedException;
import tn.esprit.projetintegre.security.SecurityUtil;
import tn.esprit.projetintegre.enums.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.projetintegre.entities.CampingService;
import tn.esprit.projetintegre.entities.Event;
import tn.esprit.projetintegre.entities.EventServiceEntity;
import tn.esprit.projetintegre.exception.BusinessException;
import tn.esprit.projetintegre.exception.ResourceNotFoundException;
import tn.esprit.projetintegre.repositories.CampingServiceRepository;
import tn.esprit.projetintegre.repositories.EventRepository;
import tn.esprit.projetintegre.repositories.EventServiceEntityRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceAssignmentService {

    private final EventServiceEntityRepository eventServiceEntityRepository;
    private final EventRepository eventRepository;
    private final CampingServiceRepository campingServiceRepository;

    public EventServiceEntity assignServiceToEvent(Long eventId, Long serviceId, Long organisateurId,
            int quantiteRequise) {
        // Only ORGANIZER role can assign services to events
        if (!SecurityUtil.hasRole(Role.ORGANIZER)) {
            throw new AccessDeniedException("Only ORGANIZER can assign services to events");
        }
        if (quantiteRequise < 1) {
            throw new BusinessException("La quantité requise doit être au moins 1");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé avec l'ID: " + eventId));

        if (event.getOrganizer() == null || !event.getOrganizer().getId().equals(organisateurId)) {
            throw new BusinessException("Seul l'organisateur qui a créé l'événement peut assigner des services");
        }

        CampingService campingService = campingServiceRepository.findById(serviceId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Service de camping non trouvé avec l'ID: " + serviceId));

        if (campingService.getIsAvailable() == null || !campingService.getIsAvailable()) {
            throw new BusinessException("Ce service n'est pas disponible pour le moment");
        }

        if (eventServiceEntityRepository.existsByEventIdAndServiceId(eventId, serviceId)) {
            throw new BusinessException("Ce service est déjà assigné à cet événement");
        }

        EventServiceEntity assignment = EventServiceEntity.builder()
                .event(event)
                .service(campingService)
                .name(campingService.getName())
                .description(campingService.getDescription())
                .serviceType(campingService.getType())
                .price(campingService.getPrice())
                .quantiteRequise(quantiteRequise)
                .quantiteAcceptee(0)
                .quantity(1)
                .build();

        return eventServiceEntityRepository.save(assignment);
    }
}
