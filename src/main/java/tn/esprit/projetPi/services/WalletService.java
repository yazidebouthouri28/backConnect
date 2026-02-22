package tn.esprit.projetPi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.projetPi.dto.*;
import tn.esprit.projetPi.entities.*;
import tn.esprit.projetPi.exception.InsufficientFundsException;
import tn.esprit.projetPi.exception.ResourceNotFoundException;
import tn.esprit.projetPi.repositories.TransactionRepository;
import tn.esprit.projetPi.repositories.WalletRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WalletDTO getWallet(String userId) {
        Wallet wallet = getOrCreateWallet(userId);
        return toDTO(wallet);
    }

    public WalletDTO getWalletByUserId(String userId) {
        Wallet wallet = getOrCreateWallet(userId);
        return toDTO(wallet);
    }

    public WalletDTO getWalletById(String walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found: " + walletId));
        return toDTO(wallet);
    }

    public WalletDTO createWallet(String userId) {
        return toDTO(getOrCreateWallet(userId));
    }

    @Transactional
    public WalletDTO addFunds(String userId, WalletTransactionRequest request) {
        return deposit(userId, request.getAmount(), request.getDescription());
    }

    @Transactional
    public WalletDTO deductFunds(String userId, WalletTransactionRequest request) {
        return withdraw(userId, request.getAmount(), request.getDescription());
    }

    public List<Transaction> getTransactionHistory(String userId) {
        return transactionRepository.findByUserId(userId);
    }

    @Transactional
    public WalletDTO deposit(String userId, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        Wallet wallet = getOrCreateWallet(userId);
        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        // Record transaction
        createTransaction(userId, wallet.getId(), null, amount, TransactionType.DEPOSIT,
                TransactionStatus.COMPLETED, description);

        log.info("Deposited {} to wallet of user {}", amount, userId);
        return toDTO(wallet);
    }

    @Transactional
    public WalletDTO withdraw(String userId, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        Wallet wallet = getOrCreateWallet(userId);
        
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient balance. Available: " + wallet.getBalance());
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        // Record transaction
        createTransaction(userId, wallet.getId(), null, amount.negate(), TransactionType.WITHDRAWAL,
                TransactionStatus.COMPLETED, description);

        log.info("Withdrew {} from wallet of user {}", amount, userId);
        return toDTO(wallet);
    }

    @Transactional
    public WalletDTO processPayment(String userId, String orderId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }

        Wallet wallet = getOrCreateWallet(userId);
        
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient balance for payment. Available: " + wallet.getBalance());
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        // Record transaction
        createTransaction(userId, wallet.getId(), orderId, amount.negate(), TransactionType.PAYMENT,
                TransactionStatus.COMPLETED, "Payment for order " + orderId);

        log.info("Processed payment of {} for order {} from user {}", amount, orderId, userId);
        return toDTO(wallet);
    }

    @Transactional
    public WalletDTO processRefund(String userId, BigDecimal amount, String orderId) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Refund amount must be positive");
        }

        Wallet wallet = getOrCreateWallet(userId);
        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        // Record transaction
        createTransaction(userId, wallet.getId(), orderId, amount, TransactionType.REFUND,
                TransactionStatus.COMPLETED, "Refund for order " + orderId);

        log.info("Processed refund of {} to wallet of user {} for order {}", amount, userId, orderId);
        return toDTO(wallet);
    }

    @Transactional
    public WalletDTO addSellerEarnings(String sellerId, String orderId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Earnings amount must be positive");
        }

        // Calculate platform fee (e.g., 10%)
        BigDecimal platformFee = amount.multiply(BigDecimal.valueOf(0.10));
        BigDecimal netAmount = amount.subtract(platformFee);

        Wallet wallet = getOrCreateWallet(sellerId);
        wallet.setBalance(wallet.getBalance().add(netAmount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        // Record transaction with fee
        Transaction transaction = new Transaction();
        transaction.setUserId(sellerId);
        transaction.setWalletId(wallet.getId());
        transaction.setOrderId(orderId);
        transaction.setAmount(amount);
        transaction.setFee(platformFee);
        transaction.setNetAmount(netAmount);
        transaction.setType(TransactionType.SALE);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setCurrency(wallet.getCurrency());
        transaction.setDescription("Earnings from order " + orderId);
        transaction.setReference(UUID.randomUUID().toString());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setProcessedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        log.info("Added earnings of {} (net: {}) to seller {} for order {}", amount, netAmount, sellerId, orderId);
        return toDTO(wallet);
    }

    public List<TransactionDTO> getTransactions(String userId) {
        return transactionRepository.findByUserId(userId).stream()
                .map(this::toTransactionDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> getTransactionsByWallet(String walletId) {
        return transactionRepository.findByWalletId(walletId).stream()
                .map(this::toTransactionDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> getTransactionsByDateRange(String userId, LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findByUserIdAndDateRange(userId, start, end).stream()
                .map(this::toTransactionDTO)
                .collect(Collectors.toList());
    }

    private Wallet getOrCreateWallet(String userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Wallet newWallet = new Wallet();
                    newWallet.setUserId(userId);
                    newWallet.setBalance(BigDecimal.ZERO);
                    newWallet.setCurrency("USD");
                    newWallet.setCreatedAt(LocalDateTime.now());
                    log.info("Created new wallet for user {}", userId);
                    return walletRepository.save(newWallet);
                });
    }

    private void createTransaction(String userId, String walletId, String orderId, BigDecimal amount,
                                   TransactionType type, TransactionStatus status, String description) {
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setWalletId(walletId);
        transaction.setOrderId(orderId);
        transaction.setAmount(amount.abs());
        transaction.setNetAmount(amount);
        transaction.setFee(BigDecimal.ZERO);
        transaction.setType(type);
        transaction.setStatus(status);
        transaction.setCurrency("USD");
        transaction.setDescription(description);
        transaction.setReference(UUID.randomUUID().toString());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setProcessedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    private WalletDTO toDTO(Wallet wallet) {
        return WalletDTO.builder()
                .id(wallet.getId())
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .build();
    }

    private TransactionDTO toTransactionDTO(Transaction transaction) {
        return TransactionDTO.builder()
                .id(transaction.getId())
                .userId(transaction.getUserId())
                .orderId(transaction.getOrderId())
                .walletId(transaction.getWalletId())
                .amount(transaction.getAmount())
                .fee(transaction.getFee())
                .netAmount(transaction.getNetAmount())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .currency(transaction.getCurrency())
                .description(transaction.getDescription())
                .reference(transaction.getReference())
                .transactionDate(transaction.getTransactionDate())
                .processedAt(transaction.getProcessedAt())
                .originalTransactionId(transaction.getOriginalTransactionId())
                .refundReason(transaction.getRefundReason())
                .build();
    }
}
