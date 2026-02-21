package com.camping.projet.service;

import com.camping.projet.dto.request.PromotionRequest;
import com.camping.projet.dto.response.PromotionResponse;
import com.camping.projet.dto.response.UtilisationPromotionResponse;
import com.camping.projet.dto.response.ValidationResultDTO;
import java.math.BigDecimal;
import java.util.List;

public interface IPromotionService {
    PromotionResponse createPromotion(PromotionRequest request);

    PromotionResponse updatePromotion(Long id, PromotionRequest request);

    void deletePromotion(Long id);

    PromotionResponse getPromotionByCode(String codePromo);

    List<PromotionResponse> getAllPromotions();

    List<PromotionResponse> getActivePromotions();

    ValidationResultDTO validatePromotion(String codePromo, Long userId, BigDecimal montantTotal);

    UtilisationPromotionResponse applyPromotion(String codePromo, Long userId, Long reservationId,
            BigDecimal montantTotal);

    List<UtilisationPromotionResponse> getUsageHistory(Long promotionId);
}
