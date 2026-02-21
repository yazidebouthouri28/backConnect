package com.camping.projet.controller;

import com.camping.projet.dto.request.AlerteUrgenceRequest;
import com.camping.projet.dto.response.AlerteUrgenceResponse;
import com.camping.projet.enums.StatutAlerte;
import com.camping.projet.service.IAlerteUrgenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlerteUrgenceController {

    private final IAlerteUrgenceService alerteService;

    @PostMapping("/trigger")
    public ResponseEntity<AlerteUrgenceResponse> triggerAlert(@Valid @RequestBody AlerteUrgenceRequest request) {
        return new ResponseEntity<>(alerteService.triggerAlert(request), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AlerteUrgenceResponse> updateStatus(@PathVariable Long id,
            @RequestParam StatutAlerte statut) {
        return ResponseEntity.ok(alerteService.updateAlertStatus(id, statut));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlerteUrgenceResponse> updateDetails(@PathVariable Long id,
            @Valid @RequestBody AlerteUrgenceRequest request) {
        return ResponseEntity.ok(alerteService.updateAlertDetails(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlerteUrgenceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(alerteService.getAlertById(id));
    }

    @GetMapping("/active")
    public ResponseEntity<List<AlerteUrgenceResponse>> getActive() {
        return ResponseEntity.ok(alerteService.getActiveAlerts());
    }

    @GetMapping("/history")
    public ResponseEntity<List<AlerteUrgenceResponse>> getHistory() {
        return ResponseEntity.ok(alerteService.getAlertHistory());
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<Void> resolve(
            @PathVariable Long id,
            @RequestParam String description,
            @RequestParam int nbBlesses) {
        alerteService.resolveAlert(id, description, nbBlesses);
        return ResponseEntity.ok().build();
    }
}
