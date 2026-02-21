package com.camping.projet.controller;

import com.camping.projet.dto.request.EventServiceRequest;
import com.camping.projet.dto.response.EventServiceResponse;
import com.camping.projet.service.IEventServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/event-services")
@RequiredArgsConstructor
public class EventServiceController {

    private final IEventServiceService eventService;

    @PostMapping
    public ResponseEntity<EventServiceResponse> assignService(@Valid @RequestBody EventServiceRequest request) {
        return new ResponseEntity<>(eventService.assignServiceToEvent(request), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeService(@PathVariable Long id) {
        eventService.removeServiceFromEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<EventServiceResponse>> getByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getServicesByEvent(eventId));
    }

    @GetMapping("/available")
    public ResponseEntity<List<EventServiceResponse>> getAvailable() {
        return ResponseEntity.ok(eventService.getAvailableEventServices());
    }

    @PatchMapping("/{id}/quantity")
    public ResponseEntity<Void> updateQuantity(@PathVariable Long id, @RequestParam Integer quantity) {
        eventService.updateRequiredQuantity(id, quantity);
        return ResponseEntity.ok().build();
    }
}
