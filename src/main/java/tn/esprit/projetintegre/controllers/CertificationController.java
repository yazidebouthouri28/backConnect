package tn.esprit.projetintegre.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetintegre.entities.Certification;
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
    public List<Certification> getBySite(@PathVariable Long siteId) {
        return certificationService.getCertificationsBySite(siteId);
    }

    @GetMapping("/status/{status}")
    public List<Certification> getByStatus(@PathVariable CertificationStatus status) {
        return certificationService.getCertificationsByStatus(status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Certification> getById(@PathVariable Long id) {
        return ResponseEntity.ok(certificationService.getCertificationById(id));
    }

    @PostMapping("/site/{siteId}")
    public ResponseEntity<Certification> create(@PathVariable Long siteId,
                                                @RequestBody Certification certification) {
        return ResponseEntity.ok(certificationService.createCertification(siteId, certification));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Certification> updateStatus(@PathVariable Long id,
                                                      @RequestParam CertificationStatus status) {
        return ResponseEntity.ok(certificationService.updateStatus(id, status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Certification> update(@PathVariable Long id,
                                                @RequestBody Certification certification) {
        return ResponseEntity.ok(certificationService.updateCertification(id, certification));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        certificationService.deleteCertification(id);
        return ResponseEntity.noContent().build();
    }
}