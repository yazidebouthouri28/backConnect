package tn.esprit.projetintegre.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.projetintegre.entities.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findBySite_Id(Long siteId);
}