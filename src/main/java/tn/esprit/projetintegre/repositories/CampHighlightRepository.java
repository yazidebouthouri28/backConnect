package tn.esprit.projetintegre.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.projetintegre.entities.CampHighlight;
import tn.esprit.projetintegre.enums.HighlightCategory;


import java.util.List;

public interface CampHighlightRepository extends JpaRepository<CampHighlight, Long> {
    List<CampHighlight> findBySite_SiteId(Long siteId);
    List<CampHighlight> findBySite_SiteIdAndCategory(Long siteId, HighlightCategory category);
}