package tn.esprit.backconnect.services;

import tn.esprit.backconnect.entities.Event;
import tn.esprit.backconnect.entities.EventStatus;

import java.util.List;

public interface IEventService {
    List<Event> getAllEvents();

    Event getEventById(Long id);

    Event createEvent(Event event);

    Event updateEvent(Long id, Event event);

    void deleteEvent(Long id);

    List<Event> getEventsByStatus(EventStatus status);

    List<Event> getEventsByOrganizer(Long organizerId);

    List<Event> getEventsByType(Long eventTypeId);
}
