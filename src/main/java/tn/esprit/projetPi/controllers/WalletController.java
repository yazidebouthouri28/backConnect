package tn.esprit.projetPi.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetPi.dto.ApiResponse;
import tn.esprit.projetPi.dto.WalletDTO;
import tn.esprit.projetPi.dto.WalletTransactionRequest;
import tn.esprit.projetPi.entities.Transaction;
import tn.esprit.projetPi.services.WalletService;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    public ResponseEntity<ApiResponse<WalletDTO>> getMyWallet(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        WalletDTO wallet = walletService.getWalletByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(wallet));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<WalletDTO>> getWalletByUser(@PathVariable String userId) {
        WalletDTO wallet = walletService.getWalletByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(wallet));
    }

    @PostMapping("/add-funds")
    public ResponseEntity<ApiResponse<WalletDTO>> addFunds(
            Authentication authentication,
            @Valid @RequestBody WalletTransactionRequest request) {
        String userId = (String) authentication.getPrincipal();
        WalletDTO wallet = walletService.addFunds(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Funds added successfully", wallet));
    }

    @PostMapping("/deduct")
    public ResponseEntity<ApiResponse<WalletDTO>> deductFunds(
            Authentication authentication,
            @Valid @RequestBody WalletTransactionRequest request) {
        String userId = (String) authentication.getPrincipal();
        WalletDTO wallet = walletService.deductFunds(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Funds deducted successfully", wallet));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<Transaction>>> getTransactionHistory(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        List<Transaction> transactions = walletService.getTransactionHistory(userId);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    @GetMapping("/transactions/{userId}")
    public ResponseEntity<ApiResponse<List<Transaction>>> getTransactionHistoryByUser(@PathVariable String userId) {
        List<Transaction> transactions = walletService.getTransactionHistory(userId);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }
}
