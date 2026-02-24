package tn.esprit.backconnect.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.backconnect.entities.Event;
import tn.esprit.backconnect.entities.EventStatus;
import tn.esprit.backconnect.services.IEventService;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Event management APIs")
public class EventController {

    private final IEventService eventService;

    @GetMapping
    @Operation(summary = "Get all events")
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new event")
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        return new ResponseEntity<>(eventService.createEvent(event), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an event")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event event) {
        return ResponseEntity.ok(eventService.updateEvent(id, event));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an event")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get events by status")
    public ResponseEntity<List<Event>> getEventsByStatus(@PathVariable EventStatus status) {
        return ResponseEntity.ok(eventService.getEventsByStatus(status));
    }

    @GetMapping("/organizer/{organizerId}")
    @Operation(summary = "Get events by organizer")
    public ResponseEntity<List<Event>> getEventsByOrganizer(@PathVariable Long organizerId) {
        return ResponseEntity.ok(eventService.getEventsByOrganizer(organizerId));
    }

    @GetMapping("/type/{eventTypeId}")
    @Operation(summary = "Get events by type")
    public ResponseEntity<List<Event>> getEventsByType(@PathVariable Long eventTypeId) {
        return ResponseEntity.ok(eventService.getEventsByType(eventTypeId));
    }
}
