package tn.esprit.projetPi.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.projetPi.dto.WalletDTO;
import tn.esprit.projetPi.dto.WalletTransactionRequest;
import tn.esprit.projetPi.entities.Transaction;
import tn.esprit.projetPi.entities.Wallet;
import tn.esprit.projetPi.exception.InsufficientFundsException;
import tn.esprit.projetPi.exception.ResourceNotFoundException;
import tn.esprit.projetPi.repositories.TransactionRepository;
import tn.esprit.projetPi.repositories.WalletRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WalletDTO getWalletByUserId(String userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));
        return toDTO(wallet);
    }

    public WalletDTO createWallet(String userId) {
        if (walletRepository.existsByUserId(userId)) {
            return getWalletByUserId(userId);
        }

        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency("USD");
        wallet.setCreatedAt(LocalDateTime.now());
        wallet.setUpdatedAt(LocalDateTime.now());

        Wallet saved = walletRepository.save(wallet);
        return toDTO(saved);
    }

    public WalletDTO addFunds(String userId, WalletTransactionRequest request) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        wallet.setUpdatedAt(LocalDateTime.now());

        // Record transaction
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setAmount(request.getAmount().doubleValue());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setStatus("COMPLETED");
        transactionRepository.save(transaction);

        Wallet saved = walletRepository.save(wallet);
        return toDTO(saved);
    }

    public WalletDTO deductFunds(String userId, WalletTransactionRequest request) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds. Available: " + wallet.getBalance());
        }

        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        wallet.setUpdatedAt(LocalDateTime.now());

        // Record transaction
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setAmount(-request.getAmount().doubleValue());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setStatus("COMPLETED");
        transactionRepository.save(transaction);

        Wallet saved = walletRepository.save(wallet);
        return toDTO(saved);
    }

    public List<Transaction> getTransactionHistory(String userId) {
        return transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);
    }

    private WalletDTO toDTO(Wallet wallet) {
        WalletDTO dto = new WalletDTO();
        dto.setId(wallet.getId());
        dto.setUserId(wallet.getUserId());
        dto.setBalance(wallet.getBalance());
        dto.setCurrency(wallet.getCurrency());
        dto.setCreatedAt(wallet.getCreatedAt());
        dto.setUpdatedAt(wallet.getUpdatedAt());
        return dto;
    }
}
