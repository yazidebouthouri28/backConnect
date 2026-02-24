package tn.esprit.backconnect.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.backconnect.entities.TicketRequest;
import tn.esprit.backconnect.services.ITicketRequestService;

import java.util.List;

@RestController
@RequestMapping("/api/ticket-requests")
@RequiredArgsConstructor
@Tag(name = "Ticket Requests", description = "Ticket request workflow APIs")
public class TicketRequestController {

    private final ITicketRequestService ticketRequestService;

    @GetMapping
    @Operation(summary = "Get all ticket requests")
    public ResponseEntity<List<TicketRequest>> getAllTicketRequests() {
        return ResponseEntity.ok(ticketRequestService.getAllTicketRequests());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ticket request by ID")
    public ResponseEntity<TicketRequest> getTicketRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketRequestService.getTicketRequestById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new ticket request")
    public ResponseEntity<TicketRequest> createTicketRequest(@RequestBody TicketRequest ticketRequest) {
        return new ResponseEntity<>(ticketRequestService.createTicketRequest(ticketRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a ticket request")
    public ResponseEntity<TicketRequest> updateTicketRequest(@PathVariable Long id,
            @RequestBody TicketRequest ticketRequest) {
        return ResponseEntity.ok(ticketRequestService.updateTicketRequest(id, ticketRequest));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a ticket request")
    public ResponseEntity<Void> deleteTicketRequest(@PathVariable Long id) {
        ticketRequestService.deleteTicketRequest(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Approve a ticket request")
    public ResponseEntity<TicketRequest> approveTicketRequest(@PathVariable Long id,
            @RequestParam(required = false) String responseMessage) {
        return ResponseEntity.ok(ticketRequestService.approveTicketRequest(id, responseMessage));
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject a ticket request")
    public ResponseEntity<TicketRequest> rejectTicketRequest(@PathVariable Long id,
            @RequestParam(required = false) String responseMessage) {
        return ResponseEntity.ok(ticketRequestService.rejectTicketRequest(id, responseMessage));
    }

    @GetMapping("/participant/{participantId}")
    @Operation(summary = "Get ticket requests by participant")
    public ResponseEntity<List<TicketRequest>> getTicketRequestsByParticipant(@PathVariable Long participantId) {
        return ResponseEntity.ok(ticketRequestService.getTicketRequestsByParticipant(participantId));
    }

    @GetMapping("/event/{eventId}")
    @Operation(summary = "Get ticket requests by event")
    public ResponseEntity<List<TicketRequest>> getTicketRequestsByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(ticketRequestService.getTicketRequestsByEvent(eventId));
    }
}
