package tn.esprit.backconnect.services;

import tn.esprit.backconnect.entities.EventType;

import java.util.List;

public interface IEventTypeService {
    List<EventType> getAllEventTypes();

    EventType getEventTypeById(Long id);

    EventType createEventType(EventType eventType);

    EventType updateEventType(Long id, EventType eventType);

    void deleteEventType(Long id);
}
