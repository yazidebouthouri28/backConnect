package tn.esprit.projetPi.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.Transaction;
import tn.esprit.projetPi.entities.TransactionStatus;
import tn.esprit.projetPi.entities.TransactionType;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    
    List<Transaction> findByUserId(String userId);
    
    Page<Transaction> findByUserId(String userId, Pageable pageable);
    
    List<Transaction> findByWalletId(String walletId);
    
    List<Transaction> findByOrderId(String orderId);
    
    List<Transaction> findByType(TransactionType type);
    
    List<Transaction> findByStatus(TransactionStatus status);
    
    @Query("{ 'transactionDate': { $gte: ?0, $lte: ?1 } }")
    List<Transaction> findByDateRange(LocalDateTime start, LocalDateTime end);
    
    @Query("{ 'userId': ?0, 'transactionDate': { $gte: ?1, $lte: ?2 } }")
    List<Transaction> findByUserIdAndDateRange(String userId, LocalDateTime start, LocalDateTime end);
    
    List<Transaction> findByOriginalTransactionId(String originalTransactionId);
}
