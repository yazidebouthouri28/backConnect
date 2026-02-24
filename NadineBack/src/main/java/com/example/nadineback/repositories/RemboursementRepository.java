package com.example.nadineback.repositories;

import com.example.nadineback.entities.Remboursement;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RemboursementRepository extends MongoRepository<Remboursement, String> {
    List<Remboursement> findByTransactionId(String transactionId);
}
