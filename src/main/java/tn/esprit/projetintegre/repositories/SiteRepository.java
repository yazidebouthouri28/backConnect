package tn.esprit.projetintegre.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.projetintegre.entities.Site;


import java.util.List;

public interface SiteRepository extends JpaRepository<Site, Long> {
    List<Site> findByCityIgnoreCase(String city);
    List<Site> findByNameContainingIgnoreCase(String name);
}