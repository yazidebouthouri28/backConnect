package tn.esprit.projetPi.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.Transaction;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    
    List<Transaction> findByUserId(String userId);
    
    List<Transaction> findByStatus(String status);
    
    @Query("{'transactionDate': {$gte: ?0, $lte: ?1}}")
    List<Transaction> findByTransactionDateBetween(LocalDateTime start, LocalDateTime end);
    
    List<Transaction> findByUserIdOrderByTransactionDateDesc(String userId);
}
