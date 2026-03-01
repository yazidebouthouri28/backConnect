package tn.esprit.projetintegre.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.projetintegre.dto.request.CertificationRequest;
import tn.esprit.projetintegre.dto.response.CertificationResponse;
import tn.esprit.projetintegre.entities.Certification;
import tn.esprit.projetintegre.entities.Site;
import tn.esprit.projetintegre.enums.CertificationStatus;
import tn.esprit.projetintegre.mapper.SiteModuleMapper;
import tn.esprit.projetintegre.repositories.CertificationRepository;
import tn.esprit.projetintegre.repositories.SiteRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificationService {

    private final CertificationRepository certificationRepository;
    private final SiteRepository siteRepository;
    private final SiteModuleMapper siteMapper;

    public List<CertificationResponse> getCertificationsBySite(Long siteId) {
        return siteMapper.toCertificationResponseList(certificationRepository.findBySite_Id(siteId)); // Fixed
                                                                                                      // findBySite_SiteId
                                                                                                      // to
                                                                                                      // findBySite_Id
    }

    public List<CertificationResponse> getCertificationsByStatus(CertificationStatus status) {
        return siteMapper.toCertificationResponseList(certificationRepository.findByStatus(status));
    }

    public CertificationResponse getCertificationById(Long id) {
        Certification cert = certificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certification not found"));
        return siteMapper.toResponse(cert);
    }

    public CertificationResponse createCertification(Long siteId, CertificationRequest request) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new RuntimeException("Site not found"));
        Certification certification = siteMapper.toEntity(request, site);
        certification.setCertificationCode("CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        return siteMapper.toResponse(certificationRepository.save(certification));
    }

    public CertificationResponse updateStatus(Long id, CertificationStatus status) {
        Certification cert = certificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certification not found"));
        cert.setStatus(status);
        return siteMapper.toResponse(certificationRepository.save(cert));
    }

    public CertificationResponse updateCertification(Long id, CertificationRequest request) {
        Certification existing = certificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certification not found"));
        siteMapper.updateEntity(existing, request);
        return siteMapper.toResponse(certificationRepository.save(existing));
    }

    public void deleteCertification(Long id) {
        certificationRepository.deleteById(id);
    }
}