package tn.esprit.projetPi.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.projetPi.dto.EventDTO;
import tn.esprit.projetPi.dto.EventSearchRequest;
import tn.esprit.projetPi.entities.Event;
import tn.esprit.projetPi.entities.EventStatus;
import tn.esprit.projetPi.exception.ResourceNotFoundException;
import tn.esprit.projetPi.repositories.EventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<EventDTO> getPublishedEvents() {
        return eventRepository.findByIsPublishedTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<EventDTO> getFeaturedEvents() {
        return eventRepository.findByIsFeaturedTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<EventDTO> getUpcomingEvents() {
        return eventRepository.findUpcomingEvents(LocalDateTime.now()).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public EventDTO getEventById(String id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        return toDTO(event);
    }

    public List<EventDTO> getEventsByOrganizer(String organizerId) {
        return eventRepository.findByOrganizerId(organizerId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public EventDTO createEvent(EventDTO dto, String organizerId, String organizerName) {
        Event event = toEntity(dto);
        event.setOrganizerId(organizerId);
        event.setOrganizerName(organizerName);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        event.setStatus(EventStatus.DRAFT);
        event.setRegisteredCount(0);
        event.setWaitlistCount(0);
        event.setAverageRating(0.0);
        event.setReviewCount(0);
        event.setIsPublished(false);
        return toDTO(eventRepository.save(event));
    }

    public EventDTO updateEvent(String id, EventDTO dto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        
        updateEventFromDTO(event, dto);
        event.setUpdatedAt(LocalDateTime.now());
        return toDTO(eventRepository.save(event));
    }

    public void deleteEvent(String id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event not found with id: " + id);
        }
        eventRepository.deleteById(id);
    }

    public EventDTO publishEvent(String id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        event.setIsPublished(true);
        event.setStatus(EventStatus.PUBLISHED);
        event.setUpdatedAt(LocalDateTime.now());
        return toDTO(eventRepository.save(event));
    }

    public EventDTO openRegistration(String id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        event.setStatus(EventStatus.REGISTRATION_OPEN);
        event.setUpdatedAt(LocalDateTime.now());
        return toDTO(eventRepository.save(event));
    }

    public EventDTO closeRegistration(String id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        event.setStatus(EventStatus.REGISTRATION_CLOSED);
        event.setUpdatedAt(LocalDateTime.now());
        return toDTO(eventRepository.save(event));
    }

    public EventDTO cancelEvent(String id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        event.setStatus(EventStatus.CANCELLED);
        event.setUpdatedAt(LocalDateTime.now());
        return toDTO(eventRepository.save(event));
    }

    public List<EventDTO> searchEvents(EventSearchRequest request) {
        List<Event> results;
        
        if (request.getQuery() != null && !request.getQuery().isEmpty()) {
            results = eventRepository.searchEvents(request.getQuery());
        } else if (request.getType() != null) {
            results = eventRepository.findByType(request.getType());
        } else if (request.getCity() != null) {
            results = eventRepository.findByCity(request.getCity());
        } else {
            results = eventRepository.findByIsPublishedTrue();
        }
        
        // Apply filters
        if (request.getIsFree() != null && request.getIsFree()) {
            results = results.stream().filter(e -> Boolean.TRUE.equals(e.getIsFree())).collect(Collectors.toList());
        }
        
        if (request.getMaxPrice() != null) {
            results = results.stream()
                    .filter(e -> e.getPrice() == null || e.getPrice() <= request.getMaxPrice())
                    .collect(Collectors.toList());
        }
        
        if (request.getIsVirtual() != null) {
            results = results.stream()
                    .filter(e -> request.getIsVirtual().equals(e.getIsVirtual()))
                    .collect(Collectors.toList());
        }
        
        return results.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public EventDTO toggleFeatured(String id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        event.setIsFeatured(!Boolean.TRUE.equals(event.getIsFeatured()));
        event.setUpdatedAt(LocalDateTime.now());
        return toDTO(eventRepository.save(event));
    }

    public void incrementRegistrationCount(String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        event.setRegisteredCount(event.getRegisteredCount() + 1);
        
        // Check if sold out
        if (event.getCapacity() != null && event.getRegisteredCount() >= event.getCapacity()) {
            event.setStatus(EventStatus.SOLD_OUT);
        }
        eventRepository.save(event);
    }

    public void decrementRegistrationCount(String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        if (event.getRegisteredCount() > 0) {
            event.setRegisteredCount(event.getRegisteredCount() - 1);
            
            // Reopen registration if was sold out
            if (event.getStatus() == EventStatus.SOLD_OUT) {
                event.setStatus(EventStatus.REGISTRATION_OPEN);
            }
        }
        eventRepository.save(event);
    }

    public boolean hasAvailableSpots(String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        return event.getCapacity() == null || event.getRegisteredCount() < event.getCapacity();
    }

    public void updateRating(String eventId, Double newRating, Integer newReviewCount) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        event.setAverageRating(newRating);
        event.setReviewCount(newReviewCount);
        eventRepository.save(event);
    }

    private EventDTO toDTO(Event event) {
        Integer availableSpots = null;
        if (event.getCapacity() != null) {
            availableSpots = event.getCapacity() - (event.getRegisteredCount() != null ? event.getRegisteredCount() : 0);
        }
        
        return EventDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .shortDescription(event.getShortDescription())
                .type(event.getType())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .timezone(event.getTimezone())
                .venueName(event.getVenueName())
                .address(event.getAddress())
                .city(event.getCity())
                .state(event.getState())
                .country(event.getCountry())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .isVirtual(event.getIsVirtual())
                .virtualLink(event.getVirtualLink())
                .capacity(event.getCapacity())
                .registeredCount(event.getRegisteredCount())
                .waitlistCount(event.getWaitlistCount())
                .registrationRequired(event.getRegistrationRequired())
                .registrationDeadline(event.getRegistrationDeadline())
                .availableSpots(availableSpots)
                .price(event.getPrice())
                .isFree(event.getIsFree())
                .currency(event.getCurrency())
                .ticketTiers(event.getTicketTiers())
                .images(event.getImages())
                .featuredImage(event.getFeaturedImage())
                .organizerId(event.getOrganizerId())
                .organizerName(event.getOrganizerName())
                .organizerEmail(event.getOrganizerEmail())
                .organizerPhone(event.getOrganizerPhone())
                .sponsorIds(event.getSponsorIds())
                .status(event.getStatus())
                .isFeatured(event.getIsFeatured())
                .isPublished(event.getIsPublished())
                .schedule(event.getSchedule())
                .averageRating(event.getAverageRating())
                .reviewCount(event.getReviewCount())
                .tags(event.getTags())
                .categories(event.getCategories())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }

    private Event toEntity(EventDTO dto) {
        Event event = new Event();
        updateEventFromDTO(event, dto);
        return event;
    }

    private void updateEventFromDTO(Event event, EventDTO dto) {
        if (dto.getTitle() != null) event.setTitle(dto.getTitle());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (dto.getShortDescription() != null) event.setShortDescription(dto.getShortDescription());
        if (dto.getType() != null) event.setType(dto.getType());
        if (dto.getStartDate() != null) event.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) event.setEndDate(dto.getEndDate());
        if (dto.getTimezone() != null) event.setTimezone(dto.getTimezone());
        if (dto.getVenueName() != null) event.setVenueName(dto.getVenueName());
        if (dto.getAddress() != null) event.setAddress(dto.getAddress());
        if (dto.getCity() != null) event.setCity(dto.getCity());
        if (dto.getState() != null) event.setState(dto.getState());
        if (dto.getCountry() != null) event.setCountry(dto.getCountry());
        if (dto.getLatitude() != null) event.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null) event.setLongitude(dto.getLongitude());
        if (dto.getIsVirtual() != null) event.setIsVirtual(dto.getIsVirtual());
        if (dto.getVirtualLink() != null) event.setVirtualLink(dto.getVirtualLink());
        if (dto.getCapacity() != null) event.setCapacity(dto.getCapacity());
        if (dto.getRegistrationRequired() != null) event.setRegistrationRequired(dto.getRegistrationRequired());
        if (dto.getRegistrationDeadline() != null) event.setRegistrationDeadline(dto.getRegistrationDeadline());
        if (dto.getPrice() != null) event.setPrice(dto.getPrice());
        if (dto.getIsFree() != null) event.setIsFree(dto.getIsFree());
        if (dto.getCurrency() != null) event.setCurrency(dto.getCurrency());
        if (dto.getTicketTiers() != null) event.setTicketTiers(dto.getTicketTiers());
        if (dto.getImages() != null) event.setImages(dto.getImages());
        if (dto.getFeaturedImage() != null) event.setFeaturedImage(dto.getFeaturedImage());
        if (dto.getOrganizerEmail() != null) event.setOrganizerEmail(dto.getOrganizerEmail());
        if (dto.getOrganizerPhone() != null) event.setOrganizerPhone(dto.getOrganizerPhone());
        if (dto.getSponsorIds() != null) event.setSponsorIds(dto.getSponsorIds());
        if (dto.getIsFeatured() != null) event.setIsFeatured(dto.getIsFeatured());
        if (dto.getSchedule() != null) event.setSchedule(dto.getSchedule());
        if (dto.getTags() != null) event.setTags(dto.getTags());
        if (dto.getCategories() != null) event.setCategories(dto.getCategories());
    }
}
