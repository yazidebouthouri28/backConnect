package tn.esprit.backconnect.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.backconnect.entities.EventType;
import tn.esprit.backconnect.repositories.EventTypeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventTypeServiceImpl implements IEventTypeService {

    private final EventTypeRepository eventTypeRepository;

    @Override
    public List<EventType> getAllEventTypes() {
        return eventTypeRepository.findAll();
    }

    @Override
    public EventType getEventTypeById(Long id) {
        return eventTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EventType not found with id: " + id));
    }

    @Override
    public EventType createEventType(EventType eventType) {
        return eventTypeRepository.save(eventType);
    }

    @Override
    public EventType updateEventType(Long id, EventType eventType) {
        EventType existing = getEventTypeById(id);
        existing.setName(eventType.getName());
        existing.setDescription(eventType.getDescription());
        existing.setIconUrl(eventType.getIconUrl());
        return eventTypeRepository.save(existing);
    }

    @Override
    public void deleteEventType(Long id) {
        eventTypeRepository.deleteById(id);
    }
}
