package tn.esprit.projetintegre.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.projetintegre.entities.RouteGuide;


import java.util.List;

public interface RouteGuideRepository extends JpaRepository<RouteGuide, Long> {
    List<RouteGuide> findBySite_SiteId(Long siteId);
    List<RouteGuide> findByOriginCityIgnoreCase(String originCity);
}