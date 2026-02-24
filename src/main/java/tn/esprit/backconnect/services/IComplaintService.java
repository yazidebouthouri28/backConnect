package tn.esprit.backconnect.services;

import tn.esprit.backconnect.entities.Complaint;
import tn.esprit.backconnect.entities.ComplaintStatus;

import java.util.List;

public interface IComplaintService {
    List<Complaint> getAllComplaints();

    Complaint getComplaintById(Long id);

    Complaint createComplaint(Complaint complaint);

    Complaint updateComplaint(Long id, Complaint complaint);

    void deleteComplaint(Long id);

    List<Complaint> getComplaintsByEvent(Long eventId);

    List<Complaint> getComplaintsByParticipant(Long participantId);

    Complaint updateComplaintStatus(Long id, ComplaintStatus status);
}
