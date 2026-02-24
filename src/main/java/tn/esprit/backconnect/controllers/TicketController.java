package tn.esprit.backconnect.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.backconnect.entities.Ticket;
import tn.esprit.backconnect.services.ITicketService;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "Ticket management APIs")
public class TicketController {

    private final ITicketService ticketService;

    @GetMapping
    @Operation(summary = "Get all tickets")
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ticket by ID")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new ticket")
    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
        return new ResponseEntity<>(ticketService.createTicket(ticket), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a ticket")
    public ResponseEntity<Ticket> updateTicket(@PathVariable Long id, @RequestBody Ticket ticket) {
        return ResponseEntity.ok(ticketService.updateTicket(id, ticket));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a ticket")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "Get tickets by reservation")
    public ResponseEntity<List<Ticket>> getTicketsByReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(ticketService.getTicketsByReservation(reservationId));
    }

    @GetMapping("/number/{ticketNumber}")
    @Operation(summary = "Get ticket by number")
    public ResponseEntity<Ticket> getTicketByNumber(@PathVariable String ticketNumber) {
        return ResponseEntity.ok(ticketService.getTicketByNumber(ticketNumber));
    }
}
