package com.example.nadineback.repositories;

import com.example.nadineback.entities.Abonnement;
import com.example.nadineback.entities.StatutAbonnement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AbonnementRepository extends MongoRepository<Abonnement, String> {
    List<Abonnement> findByUserId(String userId);

    List<Abonnement> findByStatut(StatutAbonnement statut);
}
