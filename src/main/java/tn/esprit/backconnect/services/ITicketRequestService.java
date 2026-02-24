package tn.esprit.backconnect.services;

import tn.esprit.backconnect.entities.TicketRequest;

import java.util.List;

public interface ITicketRequestService {
    List<TicketRequest> getAllTicketRequests();

    TicketRequest getTicketRequestById(Long id);

    TicketRequest createTicketRequest(TicketRequest ticketRequest);

    TicketRequest updateTicketRequest(Long id, TicketRequest ticketRequest);

    void deleteTicketRequest(Long id);

    TicketRequest approveTicketRequest(Long id, String responseMessage);

    TicketRequest rejectTicketRequest(Long id, String responseMessage);

    List<TicketRequest> getTicketRequestsByParticipant(Long participantId);

    List<TicketRequest> getTicketRequestsByEvent(Long eventId);
}
