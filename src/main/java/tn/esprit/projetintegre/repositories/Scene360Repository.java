package tn.esprit.projetintegre.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.projetintegre.entities.Scene360;


import java.util.List;

public interface Scene360Repository extends JpaRepository<Scene360, Long> {
    List<Scene360> findByTour_VirtualTourIdOrderBySceneOrderAsc(Long tourId);
}