package tn.esprit.projetintegre.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetintegre.dto.request.CertificationRequest;
import tn.esprit.projetintegre.dto.response.CertificationResponse;
import tn.esprit.projetintegre.enums.CertificationStatus;
import tn.esprit.projetintegre.services.CertificationService;

import java.util.List;

@RestController
@RequestMapping("/api/certifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CertificationController {

    private final CertificationService certificationService;

    @GetMapping("/site/{siteId}")
    public List<CertificationResponse> getCertificationsBySite(@PathVariable Long siteId) {
        return certificationService.getCertificationsBySite(siteId);
    }

    @GetMapping("/status/{status}")
    public List<CertificationResponse> getCertificationsByStatus(@PathVariable CertificationStatus status) {
        return certificationService.getCertificationsByStatus(status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificationResponse> getCertificationById(@PathVariable Long id) {
        return ResponseEntity.ok(certificationService.getCertificationById(id));
    }

    @PostMapping("/site/{siteId}")
    public ResponseEntity<CertificationResponse> createCertification(@PathVariable Long siteId,
            @Valid @RequestBody CertificationRequest request) {
        return ResponseEntity.ok(certificationService.createCertification(siteId, request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<CertificationResponse> updateStatus(@PathVariable Long id,
            @RequestParam CertificationStatus status) {
        return ResponseEntity.ok(certificationService.updateStatus(id, status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CertificationResponse> updateCertification(@PathVariable Long id,
            @Valid @RequestBody CertificationRequest request) {
        return ResponseEntity.ok(certificationService.updateCertification(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCertification(@PathVariable Long id) {
        certificationService.deleteCertification(id);
        return ResponseEntity.noContent().build();
    }
}