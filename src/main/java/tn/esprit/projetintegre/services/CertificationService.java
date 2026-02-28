package tn.esprit.projetintegre.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.projetintegre.entities.Certification;
import tn.esprit.projetintegre.entities.Site;
import tn.esprit.projetintegre.enums.CertificationStatus;
import tn.esprit.projetintegre.repositories.CertificationRepository;
import tn.esprit.projetintegre.repositories.SiteRepository;


import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificationService {

    private final CertificationRepository certificationRepository;
    private final SiteRepository siteRepository;

    public List<Certification> getCertificationsBySite(Long siteId) {
        return certificationRepository.findBySite_SiteId(siteId);
    }

    public List<Certification> getCertificationsByStatus(CertificationStatus status) {
        return certificationRepository.findByStatus(status);
    }

    public Certification getCertificationById(Long id) {
        return certificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certification not found"));
    }

    public Certification createCertification(Long siteId, Certification certification) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new RuntimeException("Site not found"));
        certification.setSite(site);
        return certificationRepository.save(certification);
    }

    public Certification updateStatus(Long id, CertificationStatus status) {
        Certification cert = getCertificationById(id);
        cert.setStatus(status);
        return certificationRepository.save(cert);
    }

    public Certification updateCertification(Long id, Certification updated) {
        Certification existing = getCertificationById(id);
        existing.setStatus(updated.getStatus());
        existing.setScore(updated.getScore());
        existing.setIssueDate(updated.getIssueDate());
        existing.setExpiryDate(updated.getExpiryDate());
        return certificationRepository.save(existing);
    }

    public void deleteCertification(Long id) {
        certificationRepository.deleteById(id);
    }
}