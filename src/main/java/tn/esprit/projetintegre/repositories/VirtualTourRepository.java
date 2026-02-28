package tn.esprit.projetintegre.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.projetintegre.entities.VirtualTour;


import java.util.List;

public interface VirtualTourRepository extends JpaRepository<VirtualTour, Long> {
    List<VirtualTour> findBySite_SiteId(Long siteId);
}