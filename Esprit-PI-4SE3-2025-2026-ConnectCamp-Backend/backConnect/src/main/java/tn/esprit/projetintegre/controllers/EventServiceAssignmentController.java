package tn.esprit.projetintegre.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetintegre.dto.ApiResponse;
import tn.esprit.projetintegre.entities.EventServiceEntity;
import tn.esprit.projetintegre.services.EventServiceAssignmentService;

@RestController
@RequestMapping("/api/event-service-assignments")
@RequiredArgsConstructor
@Tag(name = "Event Service Assignments", description = "Endpoints for organizers to assign services to their events")
public class EventServiceAssignmentController {

    private final EventServiceAssignmentService assignmentService;

    @PostMapping
    @Operation(summary = "Organizer assigns a camping service to an event")
    public ResponseEntity<ApiResponse<EventServiceEntity>> assignService(
            @RequestParam Long eventId,
            @RequestParam Long serviceId,
            @RequestParam Long organisateurId,
            @RequestParam(defaultValue = "1") int quantiteRequise) {
        EventServiceEntity assignment = assignmentService.assignServiceToEvent(eventId, serviceId, organisateurId,
                quantiteRequise);
        return ResponseEntity.ok(ApiResponse.success("Service assigné à l'événement avec succès", assignment));
    }
}
