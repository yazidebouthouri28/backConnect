package com.camping.projet.service.impl;

import com.camping.projet.dto.request.PromotionRequest;
import com.camping.projet.dto.response.PromotionResponse;
import com.camping.projet.dto.response.UtilisationPromotionResponse;
import com.camping.projet.dto.response.ValidationResultDTO;
import com.camping.projet.entity.*;
import com.camping.projet.enums.CiblePromotion;
import com.camping.projet.enums.TypeCondition;
import com.camping.projet.enums.TypeReduction;
import com.camping.projet.mapper.PromotionMapper;
import com.camping.projet.mapper.UtilisationPromotionMapper;
import com.camping.projet.repository.PromotionRepository;
import com.camping.projet.repository.UserRepository;
import com.camping.projet.repository.UtilisationPromotionRepository;
import com.camping.projet.service.IPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements IPromotionService {

    private final PromotionRepository promotionRepository;
    private final UserRepository userRepository;
    private final UtilisationPromotionRepository usageRepository;
    private final PromotionMapper promotionMapper;
    private final UtilisationPromotionMapper usageMapper;

    @Override
    @Transactional
    public PromotionResponse createPromotion(PromotionRequest request) {
        if (promotionRepository.findByCodePromo(request.getCodePromo()).isPresent()) {
            throw new RuntimeException("Promotion code already exists");
        }
        Promotion promotion = promotionMapper.toEntity(request);
        promotion.setActif(true);
        promotion.setNbUtilisationsActuelles(0);
        return promotionMapper.toResponse(promotionRepository.save(promotion));
    }

    @Override
    @Transactional
    public PromotionResponse updatePromotion(Long id, PromotionRequest request) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));
        promotionMapper.updateEntity(request, promotion);
        return promotionMapper.toResponse(promotionRepository.save(promotion));
    }

    @Override
    @Transactional
    public void deletePromotion(Long id) {
        promotionRepository.deleteById(id);
    }

    @Override
    public PromotionResponse getPromotionByCode(String codePromo) {
        return promotionRepository.findByCodePromo(codePromo)
                .map(promotionMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));
    }

    @Override
    public List<PromotionResponse> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(promotionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PromotionResponse> getActivePromotions() {
        return promotionRepository.findActivePromotions(LocalDateTime.now()).stream()
                .map(promotionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ValidationResultDTO validatePromotion(String codePromo, Long userId, BigDecimal montantTotal) {
        Promotion promo = promotionRepository.findByCodePromo(codePromo)
                .orElse(null);

        if (promo == null)
            return ValidationResultDTO.builder().valid(false).message("Code invalide").reduction(BigDecimal.ZERO)
                    .montantFinal(montantTotal).build();
        if (!promo.isActif())
            return ValidationResultDTO.builder().valid(false).message("Promotion expirée ou désactivée")
                    .reduction(BigDecimal.ZERO)
                    .montantFinal(montantTotal).build();

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(promo.getDateDebut()) || (promo.getDateFin() != null && now.isAfter(promo.getDateFin()))) {
            return ValidationResultDTO.builder().valid(false).message("Promotion non disponible à cette date")
                    .reduction(BigDecimal.ZERO).montantFinal(montantTotal).build();
        }

        if (promo.getMaxUtilisations() != null && promo.getNbUtilisationsActuelles() >= promo.getMaxUtilisations()) {
            return ValidationResultDTO.builder().valid(false).message("Nombre maximal d'utilisations atteint")
                    .reduction(BigDecimal.ZERO).montantFinal(montantTotal).build();
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Target checks
        if (promo.getCible() == CiblePromotion.CLIENT_FIDEL && user.getNbReservations() < 5) {
            return ValidationResultDTO.builder().valid(false)
                    .message("Réservé aux clients fidèles (min 5 réservations)").reduction(BigDecimal.ZERO)
                    .montantFinal(montantTotal).build();
        }
        if (promo.getCible() == CiblePromotion.NOUVEAU_CLIENT && user.getNbReservations() > 0) {
            return ValidationResultDTO.builder().valid(false).message("Réservé aux nouveaux clients")
                    .reduction(BigDecimal.ZERO)
                    .montantFinal(montantTotal).build();
        }

        // Conditions checks
        for (ConditionPromotion cond : promo.getConditions()) {
            if (cond.getTypeCondition() == TypeCondition.MONTANT_MINIMAL
                    && montantTotal.compareTo(new BigDecimal(cond.getValeur())) < 0) {
                return ValidationResultDTO.builder().valid(false)
                        .message("Montant minimal non atteint: " + cond.getValeur()).reduction(BigDecimal.ZERO)
                        .montantFinal(montantTotal).build();
            }
            if (cond.getTypeCondition() == TypeCondition.NB_COMMANDES_MINIMAL
                    && user.getNbCommandes() < Integer.parseInt(cond.getValeur())) {
                return ValidationResultDTO.builder().valid(false).message("Nombre de commandes minimal non atteint")
                        .reduction(BigDecimal.ZERO).montantFinal(montantTotal).build();
            }
        }

        BigDecimal reduction = calculateDiscount(promo, montantTotal);
        return ValidationResultDTO.builder()
                .valid(true)
                .message("Promotion valide")
                .reduction(reduction)
                .montantFinal(montantTotal.subtract(reduction))
                .build();
    }

    private BigDecimal calculateDiscount(Promotion promo, BigDecimal amount) {
        if (promo.getTypeReduction() == TypeReduction.POURCENTAGE) {
            return amount.multiply(promo.getValeur()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        } else {
            return amount.min(promo.getValeur());
        }
    }

    @Override
    @Transactional
    public UtilisationPromotionResponse applyPromotion(String codePromo, Long userId, Long reservationId,
            BigDecimal montantTotal) {
        ValidationResultDTO validation = validatePromotion(codePromo, userId, montantTotal);
        if (!validation.isValid()) {
            throw new RuntimeException("Promotion non applicable: " + validation.getMessage());
        }

        Promotion promo = promotionRepository.findByCodePromo(codePromo).get();
        User user = userRepository.findById(userId).get();

        UtilisationPromotion usage = new UtilisationPromotion();
        usage.setPromotion(promo);
        usage.setUser(user);
        usage.setReservationId(reservationId);
        usage.setDateUtilisation(LocalDateTime.now());
        usage.setMontantAvantReduction(montantTotal);
        usage.setMontantReduction(validation.getReduction());
        usage.setMontantFinal(validation.getMontantFinal());

        promo.setNbUtilisationsActuelles(promo.getNbUtilisationsActuelles() + 1);
        promotionRepository.save(promo);

        return usageMapper.toResponse(usageRepository.save(usage));
    }

    @Override
    public List<UtilisationPromotionResponse> getUsageHistory(Long promotionId) {
        return usageRepository.findByPromotionId(promotionId).stream()
                .map(usageMapper::toResponse)
                .collect(Collectors.toList());
    }
}
