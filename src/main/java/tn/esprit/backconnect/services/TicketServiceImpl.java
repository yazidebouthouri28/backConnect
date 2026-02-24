package tn.esprit.backconnect.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.backconnect.entities.Ticket;
import tn.esprit.backconnect.repositories.TicketRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements ITicketService {

    private final TicketRepository ticketRepository;

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));
    }

    @Override
    public Ticket createTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket updateTicket(Long id, Ticket ticket) {
        Ticket existing = getTicketById(id);
        existing.setTicketNumber(ticket.getTicketNumber());
        existing.setStatus(ticket.getStatus());
        return ticketRepository.save(existing);
    }

    @Override
    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    @Override
    public List<Ticket> getTicketsByReservation(Long reservationId) {
        return ticketRepository.findByReservationTicketReservationId(reservationId);
    }

    @Override
    public Ticket getTicketByNumber(String ticketNumber) {
        return ticketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new RuntimeException("Ticket not found with number: " + ticketNumber));
    }
}
