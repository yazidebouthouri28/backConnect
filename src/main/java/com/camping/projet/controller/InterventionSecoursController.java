package com.camping.projet.controller;

import com.camping.projet.dto.request.InterventionSecoursRequest;
import com.camping.projet.dto.response.InterventionSecoursResponse;
import com.camping.projet.enums.StatutIntervention;
import com.camping.projet.service.IInterventionSecoursService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/interventions")
@RequiredArgsConstructor
public class InterventionSecoursController {

    private final IInterventionSecoursService interventionService;

    @PostMapping("/start")
    public ResponseEntity<InterventionSecoursResponse> start(@Valid @RequestBody InterventionSecoursRequest request) {
        return new ResponseEntity<>(interventionService.startIntervention(request), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<InterventionSecoursResponse> updateStatus(@PathVariable Long id,
            @RequestParam StatutIntervention statut) {
        return ResponseEntity.ok(interventionService.updateInterventionStatus(id, statut));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Void> complete(
            @PathVariable Long id,
            @RequestParam String rapport,
            @RequestBody List<String> materiel) {
        interventionService.completeIntervention(id, rapport, materiel);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InterventionSecoursResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(interventionService.getInterventionById(id));
    }

    @GetMapping("/alert/{alerteId}")
    public ResponseEntity<List<InterventionSecoursResponse>> getByAlert(@PathVariable Long alerteId) {
        return ResponseEntity.ok(interventionService.getInterventionsByAlert(alerteId));
    }

    @GetMapping("/team-member/{userId}")
    public ResponseEntity<List<InterventionSecoursResponse>> getByTeamMember(@PathVariable Long userId) {
        return ResponseEntity.ok(interventionService.getInterventionsByTeamMember(userId));
    }
}
