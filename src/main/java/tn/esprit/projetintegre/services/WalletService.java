package tn.esprit.projetintegre.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.projetintegre.entities.Transaction;
import tn.esprit.projetintegre.entities.User;
import tn.esprit.projetintegre.entities.Wallet;
import tn.esprit.projetintegre.enums.PaymentStatus;
import tn.esprit.projetintegre.enums.TransactionType;
import tn.esprit.projetintegre.exception.ResourceNotFoundException;
import tn.esprit.projetintegre.repositories.TransactionRepository;
import tn.esprit.projetintegre.repositories.WalletRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }

    public Wallet getWalletById(Long id) {
        return walletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + id));
    }

    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user id: " + userId));
    }

    public Wallet getOrCreateWallet(User user) {
        return walletRepository.findByUserId(user.getId())
                .orElseGet(() -> walletRepository.save(
                        Wallet.builder().user(user).build()));
    }

    public BigDecimal getBalance(Long userId) {
        Wallet wallet = getWalletByUserId(userId);
        BigDecimal balance = wallet.getBalance();
        return balance != null ? balance : BigDecimal.ZERO;
    }

    public Wallet addFunds(Long userId, BigDecimal amount) {
        Wallet wallet = getWalletByUserId(userId);
        BigDecimal currentBalance = wallet.getBalance();
        if (currentBalance == null) {
            currentBalance = BigDecimal.ZERO;
        }
        wallet.setBalance(currentBalance.add(amount));
        return walletRepository.save(wallet);
    }

    public Wallet deductFunds(Long userId, BigDecimal amount) {
        Wallet wallet = getWalletByUserId(userId);
        BigDecimal currentBalance = wallet.getBalance();
        if (currentBalance == null) {
            currentBalance = BigDecimal.ZERO;
        }
        if (currentBalance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }
        wallet.setBalance(currentBalance.subtract(amount));
        return walletRepository.save(wallet);
    }

    public Wallet deactivateWallet(Long userId) {
        Wallet wallet = getWalletByUserId(userId);
        wallet.setIsActive(false);
        return walletRepository.save(wallet);
    }

    public Wallet activateWallet(Long userId) {
        Wallet wallet = getWalletByUserId(userId);
        wallet.setIsActive(true);
        return walletRepository.save(wallet);
    }

    /**
     * Deposit with persisted transaction (ledger).
     */
    @Transactional
    public Transaction deposit(Long userId, BigDecimal amount, String description) {
        Wallet wallet = getWalletByUserId(userId);
        BigDecimal before = wallet.getBalance() != null ? wallet.getBalance() : BigDecimal.ZERO;
        BigDecimal deposited = wallet.getTotalDeposited() != null ? wallet.getTotalDeposited() : BigDecimal.ZERO;
        wallet.setBalance(before.add(amount));
        wallet.setTotalDeposited(deposited.add(amount));
        walletRepository.save(wallet);

        return transactionRepository.save(Transaction.builder()
                .wallet(wallet)
                .user(wallet.getUser())
                .type(TransactionType.CREDIT)
                .amount(amount)
                .balanceBefore(before)
                .balanceAfter(wallet.getBalance())
                .status(PaymentStatus.COMPLETED)
                .description(description)
                .processedAt(LocalDateTime.now())
                .build());
    }

    /**
     * Withdraw with persisted transaction (ledger).
     */
    @Transactional
    public Transaction withdraw(Long userId, BigDecimal amount, String description) {
        Wallet wallet = getWalletByUserId(userId);
        BigDecimal balance = wallet.getBalance() != null ? wallet.getBalance() : BigDecimal.ZERO;
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Solde insuffisant");
        }

        BigDecimal before = balance;
        BigDecimal withdrawn = wallet.getTotalWithdrawn() != null ? wallet.getTotalWithdrawn() : BigDecimal.ZERO;
        wallet.setBalance(before.subtract(amount));
        wallet.setTotalWithdrawn(withdrawn.add(amount));
        walletRepository.save(wallet);

        return transactionRepository.save(Transaction.builder()
                .wallet(wallet)
                .user(wallet.getUser())
                .type(TransactionType.DEBIT)
                .amount(amount)
                .balanceBefore(before)
                .balanceAfter(wallet.getBalance())
                .status(PaymentStatus.COMPLETED)
                .description(description)
                .processedAt(LocalDateTime.now())
                .build());
    }
}
