package tn.esprit.backconnect.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.backconnect.entities.Complaint;
import tn.esprit.backconnect.entities.ComplaintStatus;
import tn.esprit.backconnect.repositories.ComplaintRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements IComplaintService {

    private final ComplaintRepository complaintRepository;

    @Override
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    @Override
    public Complaint getComplaintById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + id));
    }

    @Override
    public Complaint createComplaint(Complaint complaint) {
        complaint.setCreationDate(LocalDateTime.now());
        complaint.setStatus(ComplaintStatus.OPEN);
        return complaintRepository.save(complaint);
    }

    @Override
    public Complaint updateComplaint(Long id, Complaint complaint) {
        Complaint existing = getComplaintById(id);
        existing.setSubject(complaint.getSubject());
        existing.setDescription(complaint.getDescription());
        return complaintRepository.save(existing);
    }

    @Override
    public void deleteComplaint(Long id) {
        complaintRepository.deleteById(id);
    }

    @Override
    public List<Complaint> getComplaintsByEvent(Long eventId) {
        return complaintRepository.findByEventId(eventId);
    }

    @Override
    public List<Complaint> getComplaintsByParticipant(Long participantId) {
        return complaintRepository.findByParticipantUserId(participantId);
    }

    @Override
    public Complaint updateComplaintStatus(Long id, ComplaintStatus status) {
        Complaint complaint = getComplaintById(id);
        complaint.setStatus(status);
        return complaintRepository.save(complaint);
    }
}
