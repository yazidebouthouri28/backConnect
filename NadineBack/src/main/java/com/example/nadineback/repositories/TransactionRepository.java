package com.example.nadineback.repositories;

import com.example.nadineback.entities.StatutTransaction;
import com.example.nadineback.entities.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByAbonnementId(String abonnementId);

    List<Transaction> findByStatut(StatutTransaction statut);
}
