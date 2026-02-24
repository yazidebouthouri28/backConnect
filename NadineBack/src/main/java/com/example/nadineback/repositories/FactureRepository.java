package com.example.nadineback.repositories;

import com.example.nadineback.entities.Facture;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactureRepository extends MongoRepository<Facture,String> {
    Facture findByTransactionId(String transactionId);
}
