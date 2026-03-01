package tn.esprit.projetintegre.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.projetintegre.entities.CertificationItem;

import java.util.List;

public interface CertificationItemRepository extends JpaRepository<CertificationItem, Long> {
    List<CertificationItem> findByCertification_Id(Long certificationId);
}