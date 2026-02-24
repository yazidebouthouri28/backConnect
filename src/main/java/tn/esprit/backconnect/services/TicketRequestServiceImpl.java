package tn.esprit.backconnect.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.backconnect.entities.TicketRequest;
import tn.esprit.backconnect.entities.TicketRequestStatus;
import tn.esprit.backconnect.repositories.TicketRequestRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketRequestServiceImpl implements ITicketRequestService {

    private final TicketRequestRepository ticketRequestRepository;

    @Override
    public List<TicketRequest> getAllTicketRequests() {
        return ticketRequestRepository.findAll();
    }

    @Override
    public TicketRequest getTicketRequestById(Long id) {
        return ticketRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TicketRequest not found with id: " + id));
    }

    @Override
    public TicketRequest createTicketRequest(TicketRequest ticketRequest) {
        ticketRequest.setRequestDate(LocalDateTime.now());
        ticketRequest.setStatus(TicketRequestStatus.PENDING);
        return ticketRequestRepository.save(ticketRequest);
    }

    @Override
    public TicketRequest updateTicketRequest(Long id, TicketRequest ticketRequest) {
        TicketRequest existing = getTicketRequestById(id);
        existing.setQuantity(ticketRequest.getQuantity());
        existing.setMessage(ticketRequest.getMessage());
        return ticketRequestRepository.save(existing);
    }

    @Override
    public void deleteTicketRequest(Long id) {
        ticketRequestRepository.deleteById(id);
    }

    @Override
    public TicketRequest approveTicketRequest(Long id, String responseMessage) {
        TicketRequest request = getTicketRequestById(id);
        request.setStatus(TicketRequestStatus.APPROVED);
        request.setResponseDate(LocalDateTime.now());
        request.setResponseMessage(responseMessage);
        return ticketRequestRepository.save(request);
    }

    @Override
    public TicketRequest rejectTicketRequest(Long id, String responseMessage) {
        TicketRequest request = getTicketRequestById(id);
        request.setStatus(TicketRequestStatus.REJECTED);
        request.setResponseDate(LocalDateTime.now());
        request.setResponseMessage(responseMessage);
        return ticketRequestRepository.save(request);
    }

    @Override
    public List<TicketRequest> getTicketRequestsByParticipant(Long participantId) {
        return ticketRequestRepository.findByParticipantUserId(participantId);
    }

    @Override
    public List<TicketRequest> getTicketRequestsByEvent(Long eventId) {
        return ticketRequestRepository.findByEventId(eventId);
    }
}
