package tn.esprit.projetintegre.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetintegre.dto.ApiResponse;
import tn.esprit.projetintegre.dto.request.EventServiceEntityRequest;
import tn.esprit.projetintegre.dto.response.EventServiceEntityResponse;
import tn.esprit.projetintegre.entities.EventServiceEntity;
import tn.esprit.projetintegre.services.EventServiceEntityService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/event-services")
@RequiredArgsConstructor
@Tag(name = "Event Services", description = "Endpoints for managing services linked to events (work roles)")
public class EventServiceEntityController {

    private final EventServiceEntityService eventServiceEntityService;

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZER')")
    @Operation(summary = "Add a B2B service to an event (Create work role)")
    public ResponseEntity<ApiResponse<EventServiceEntityResponse>> addServiceToEvent(
            @RequestBody EventServiceEntityRequest request) {
        EventServiceEntity created = eventServiceEntityService.addServiceToEvent(
                request.getEventId(),
                request.getServiceId(),
                request.getQuantiteRequise(),
                request.getDescription());
        return ResponseEntity.ok(ApiResponse.success("Service ajouté à l'événement", toResponse(created)));
    }

    @GetMapping("/event/{eventId}")
    @Operation(summary = "Get all services/work roles for a specific event")
    public ResponseEntity<ApiResponse<List<EventServiceEntityResponse>>> getServicesByEvent(
            @PathVariable Long eventId) {
        List<EventServiceEntity> services = eventServiceEntityService.getServicesByEvent(eventId);
        List<EventServiceEntityResponse> response = services.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZER')")
    @Operation(summary = "Remove a service from an event")
    public ResponseEntity<ApiResponse<Void>> removeServiceFromEvent(@PathVariable Long id) {
        eventServiceEntityService.removeServiceFromEvent(id);
        return ResponseEntity.ok(ApiResponse.success("Service retiré de l'événement", null));
    }

    private EventServiceEntityResponse toResponse(EventServiceEntity entity) {
        if (entity == null)
            return null;
        return EventServiceEntityResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .serviceType(entity.getServiceType())
                .price(entity.getPrice())
                .included(entity.getIncluded())
                .optional(entity.getOptional())
                .quantity(entity.getQuantity())
                .quantiteRequise(entity.getQuantiteRequise())
                .quantiteAcceptee(entity.getQuantiteAcceptee())
                .notes(entity.getNotes())
                .eventId(entity.getEvent() != null ? entity.getEvent().getId() : null)
                .eventTitle(entity.getEvent() != null ? entity.getEvent().getTitle() : null)
                .serviceId(entity.getService() != null ? entity.getService().getId() : null)
                .serviceName(entity.getService() != null ? entity.getService().getName() : null)
                .providerId(entity.getProvider() != null ? entity.getProvider().getId() : null)
                .providerName(entity.getProvider() != null ? entity.getProvider().getName() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
