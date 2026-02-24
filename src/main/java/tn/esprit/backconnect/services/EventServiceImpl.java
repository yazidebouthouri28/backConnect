package tn.esprit.backconnect.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.backconnect.entities.Event;
import tn.esprit.backconnect.entities.EventStatus;
import tn.esprit.backconnect.repositories.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements IEventService {

    private final EventRepository eventRepository;

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
    }

    @Override
    public Event createEvent(Event event) {
        event.setCreationDate(LocalDateTime.now());
        if (event.getStatus() == null) {
            event.setStatus(EventStatus.DRAFT);
        }
        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(Long id, Event event) {
        Event existing = getEventById(id);
        existing.setTitle(event.getTitle());
        existing.setDescription(event.getDescription());
        existing.setStartDate(event.getStartDate());
        existing.setEndDate(event.getEndDate());
        existing.setLocation(event.getLocation());
        existing.setTicketPrice(event.getTicketPrice());
        existing.setStatus(event.getStatus());
        existing.setEventType(event.getEventType());
        return eventRepository.save(existing);
    }

    @Override
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    @Override
    public List<Event> getEventsByStatus(EventStatus status) {
        return eventRepository.findByStatus(status);
    }

    @Override
    public List<Event> getEventsByOrganizer(Long organizerId) {
        return eventRepository.findByOrganizerUserId(organizerId);
    }

    @Override
    public List<Event> getEventsByType(Long eventTypeId) {
        return eventRepository.findByEventTypeId(eventTypeId);
    }
}
