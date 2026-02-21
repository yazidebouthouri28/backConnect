package com.camping.projet.controller;

import com.camping.projet.dto.request.CandidatureServiceRequest;
import com.camping.projet.dto.response.CandidatureServiceResponse;
import com.camping.projet.enums.ApplicationStatus;
import com.camping.projet.service.ICandidatureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/candidatures")
@RequiredArgsConstructor
public class CandidatureServiceController {

    private final ICandidatureService candidatureService;

    @PostMapping
    public ResponseEntity<CandidatureServiceResponse> submitCandidature(
            @Valid @RequestBody CandidatureServiceRequest request) {
        return new ResponseEntity<>(candidatureService.submitCandidature(request), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CandidatureServiceResponse> updateStatus(@PathVariable Long id,
            @RequestParam ApplicationStatus status) {
        return ResponseEntity.ok(candidatureService.updateCandidatureStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> withdrawCandidature(@PathVariable Long id) {
        candidatureService.withdrawCandidature(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidatureServiceResponse> getCandidatureById(@PathVariable Long id) {
        return ResponseEntity.ok(candidatureService.getCandidatureById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CandidatureServiceResponse>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(candidatureService.getCandidaturesByUser(userId));
    }

    @GetMapping("/event-service/{eventServiceId}")
    public ResponseEntity<List<CandidatureServiceResponse>> getByEventService(@PathVariable Long eventServiceId) {
        return ResponseEntity.ok(candidatureService.getCandidaturesByEventService(eventServiceId));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<CandidatureServiceResponse>> getByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(candidatureService.getCandidaturesByEvent(eventId));
    }
}
