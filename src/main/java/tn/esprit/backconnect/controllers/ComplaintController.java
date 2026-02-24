package tn.esprit.backconnect.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.backconnect.entities.Complaint;
import tn.esprit.backconnect.entities.ComplaintStatus;
import tn.esprit.backconnect.services.IComplaintService;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
@Tag(name = "Complaints", description = "Complaint management APIs")
public class ComplaintController {

    private final IComplaintService complaintService;

    @GetMapping
    @Operation(summary = "Get all complaints")
    public ResponseEntity<List<Complaint>> getAllComplaints() {
        return ResponseEntity.ok(complaintService.getAllComplaints());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get complaint by ID")
    public ResponseEntity<Complaint> getComplaintById(@PathVariable Long id) {
        return ResponseEntity.ok(complaintService.getComplaintById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new complaint")
    public ResponseEntity<Complaint> createComplaint(@RequestBody Complaint complaint) {
        return new ResponseEntity<>(complaintService.createComplaint(complaint), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a complaint")
    public ResponseEntity<Complaint> updateComplaint(@PathVariable Long id, @RequestBody Complaint complaint) {
        return ResponseEntity.ok(complaintService.updateComplaint(id, complaint));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a complaint")
    public ResponseEntity<Void> deleteComplaint(@PathVariable Long id) {
        complaintService.deleteComplaint(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/event/{eventId}")
    @Operation(summary = "Get complaints by event")
    public ResponseEntity<List<Complaint>> getComplaintsByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(complaintService.getComplaintsByEvent(eventId));
    }

    @GetMapping("/participant/{participantId}")
    @Operation(summary = "Get complaints by participant")
    public ResponseEntity<List<Complaint>> getComplaintsByParticipant(@PathVariable Long participantId) {
        return ResponseEntity.ok(complaintService.getComplaintsByParticipant(participantId));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update complaint status")
    public ResponseEntity<Complaint> updateComplaintStatus(@PathVariable Long id,
            @RequestParam ComplaintStatus status) {
        return ResponseEntity.ok(complaintService.updateComplaintStatus(id, status));
    }
}
