package tn.esprit.backconnect.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.backconnect.entities.EventType;
import tn.esprit.backconnect.services.IEventTypeService;

import java.util.List;

@RestController
@RequestMapping("/api/event-types")
@RequiredArgsConstructor
@Tag(name = "Event Types", description = "Event type management APIs")
public class EventTypeController {

    private final IEventTypeService eventTypeService;

    @GetMapping
    @Operation(summary = "Get all event types")
    public ResponseEntity<List<EventType>> getAllEventTypes() {
        return ResponseEntity.ok(eventTypeService.getAllEventTypes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event type by ID")
    public ResponseEntity<EventType> getEventTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(eventTypeService.getEventTypeById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new event type")
    public ResponseEntity<EventType> createEventType(@RequestBody EventType eventType) {
        return new ResponseEntity<>(eventTypeService.createEventType(eventType), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an event type")
    public ResponseEntity<EventType> updateEventType(@PathVariable Long id, @RequestBody EventType eventType) {
        return ResponseEntity.ok(eventTypeService.updateEventType(id, eventType));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an event type")
    public ResponseEntity<Void> deleteEventType(@PathVariable Long id) {
        eventTypeService.deleteEventType(id);
        return ResponseEntity.noContent().build();
    }
}
