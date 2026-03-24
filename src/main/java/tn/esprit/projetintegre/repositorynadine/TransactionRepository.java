package tn.esprit.projetintegre.repositorynadine;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.projetintegre.enums.PaymentStatus;
import tn.esprit.projetintegre.nadineentities.Transaction;
import tn.esprit.projetintegre.enums.TransactionType;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @EntityGraph(attributePaths = {"wallet", "wallet.user"})
    Optional<Transaction> findByTransactionNumber(String transactionNumber);

    @EntityGraph(attributePaths = {"wallet", "wallet.user"})
    List<Transaction> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"wallet", "wallet.user"})
    Page<Transaction> findByWalletId(Long walletId, Pageable pageable);

    @EntityGraph(attributePaths = {"wallet", "wallet.user"})
    Page<Transaction> findByType(TransactionType type, Pageable pageable);

    @EntityGraph(attributePaths = {"wallet", "wallet.user"})
    List<Transaction> findByWalletIdOrderByCreatedAtDesc(Long walletId);

    @EntityGraph(attributePaths = {"wallet", "wallet.user"})
    List<Transaction> findByStatus(PaymentStatus status);

}