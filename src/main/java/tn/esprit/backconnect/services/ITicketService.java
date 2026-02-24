package tn.esprit.backconnect.services;

import tn.esprit.backconnect.entities.Ticket;

import java.util.List;

public interface ITicketService {
    List<Ticket> getAllTickets();

    Ticket getTicketById(Long id);

    Ticket createTicket(Ticket ticket);

    Ticket updateTicket(Long id, Ticket ticket);

    void deleteTicket(Long id);

    List<Ticket> getTicketsByReservation(Long reservationId);

    Ticket getTicketByNumber(String ticketNumber);
}
