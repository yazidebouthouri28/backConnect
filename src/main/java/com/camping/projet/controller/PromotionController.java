package com.camping.projet.controller;

import com.camping.projet.dto.request.PromotionRequest;
import com.camping.projet.dto.response.PromotionResponse;
import com.camping.projet.dto.response.UtilisationPromotionResponse;
import com.camping.projet.dto.response.ValidationResultDTO;
import com.camping.projet.service.IPromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final IPromotionService promotionService;

    @PostMapping
    public ResponseEntity<PromotionResponse> createPromotion(@Valid @RequestBody PromotionRequest request) {
        return new ResponseEntity<>(promotionService.createPromotion(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromotionResponse> updatePromotion(@PathVariable Long id,
            @Valid @RequestBody PromotionRequest request) {
        return ResponseEntity.ok(promotionService.updatePromotion(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<PromotionResponse> getPromotionByCode(@PathVariable String code) {
        return ResponseEntity.ok(promotionService.getPromotionByCode(code));
    }

    @GetMapping
    public ResponseEntity<List<PromotionResponse>> getAllPromotions() {
        return ResponseEntity.ok(promotionService.getAllPromotions());
    }

    @GetMapping("/active")
    public ResponseEntity<List<PromotionResponse>> getActivePromotions() {
        return ResponseEntity.ok(promotionService.getActivePromotions());
    }

    @GetMapping("/validate")
    public ResponseEntity<ValidationResultDTO> validatePromotion(
            @RequestParam String code,
            @RequestParam Long userId,
            @RequestParam BigDecimal montant) {
        return ResponseEntity.ok(promotionService.validatePromotion(code, userId, montant));
    }

    @PostMapping("/apply")
    public ResponseEntity<UtilisationPromotionResponse> applyPromotion(
            @RequestParam String code,
            @RequestParam Long userId,
            @RequestParam Long reservationId,
            @RequestParam BigDecimal montant) {
        return ResponseEntity.ok(promotionService.applyPromotion(code, userId, reservationId, montant));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<UtilisationPromotionResponse>> getUsageHistory(@PathVariable Long id) {
        return ResponseEntity.ok(promotionService.getUsageHistory(id));
    }
}
