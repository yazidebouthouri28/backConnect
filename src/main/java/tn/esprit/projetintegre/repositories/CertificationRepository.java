package tn.esprit.projetintegre.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.projetintegre.entities.Certification;
import tn.esprit.projetintegre.enums.CertificationStatus;


import java.util.List;

public interface CertificationRepository extends JpaRepository<Certification, Long> {
    List<Certification> findBySite_SiteId(Long siteId);
    List<Certification> findByStatus(CertificationStatus status);
}