package tn.esprit.projetintegre.services;

import lombok.RequiredArgsConstructor;
import tn.esprit.projetintegre.exception.AccessDeniedException;
import tn.esprit.projetintegre.security.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import tn.esprit.projetintegre.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.projetintegre.dto.ValidationResultDTO;
import tn.esprit.projetintegre.entities.Promotion;
import tn.esprit.projetintegre.entities.User;
import tn.esprit.projetintegre.enums.PromotionType;
import tn.esprit.projetintegre.exception.ResourceNotFoundException;
import tn.esprit.projetintegre.repositories.PromotionRepository;
import tn.esprit.projetintegre.repositories.UserRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final UserRepository userRepository;

    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    public Page<Promotion> getAllPromotions(Pageable pageable) {
        return promotionRepository.findAll(pageable);
    }

    public Promotion getPromotionById(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id));
    }

    public List<Promotion> getActivePromotions() {
        return promotionRepository.findByIsActiveTrue();
    }

    public List<Promotion> getValidPromotions() {
        return promotionRepository.findActivePromotions(java.time.LocalDateTime.now());
    }

    public Promotion createPromotion(Promotion promotion) {
        // Only ADMIN can create promotions
        if (!SecurityUtil.hasRole(Role.ADMIN)) {
            throw new AccessDeniedException("Only ADMIN can create promotions");
        }
        // Business validation: endDate must be after startDate
        if (promotion.getStartDate() != null && promotion.getEndDate() != null
                && promotion.getEndDate().isBefore(promotion.getStartDate())) {
            throw new tn.esprit.projetintegre.exception.BusinessException(
                    "End date must be after start date");
        }
        promotion.setIsActive(true);
        promotion.setCurrentUsage(0);
        return promotionRepository.save(promotion);
    }

    public Promotion updatePromotion(Long id, Promotion promotionDetails) {
        // Only ADMIN can update promotions
        if (!SecurityUtil.hasRole(Role.ADMIN)) {
            throw new AccessDeniedException("Only ADMIN can update promotions");
        }
        Promotion promotion = getPromotionById(id);
        promotion.setName(promotionDetails.getName());
        promotion.setDescription(promotionDetails.getDescription());
        promotion.setType(promotionDetails.getType());
        promotion.setDiscountValue(promotionDetails.getDiscountValue());
        promotion.setMinPurchaseAmount(promotionDetails.getMinPurchaseAmount());
        promotion.setMaxDiscountAmount(promotionDetails.getMaxDiscountAmount());
        promotion.setMaxUsage(promotionDetails.getMaxUsage());
        promotion.setStartDate(promotionDetails.getStartDate());
        promotion.setEndDate(promotionDetails.getEndDate());
        promotion.setApplicableProductIds(promotionDetails.getApplicableProductIds());
        promotion.setApplicableCategoryIds(promotionDetails.getApplicableCategoryIds());
        promotion.setTargetAudience(promotionDetails.getTargetAudience());
        if (promotionDetails.getIsActive() != null) {
            promotion.setIsActive(promotionDetails.getIsActive());
        }
        // Business validation: endDate must be after startDate
        if (promotion.getStartDate() != null && promotion.getEndDate() != null
                && promotion.getEndDate().isBefore(promotion.getStartDate())) {
            throw new tn.esprit.projetintegre.exception.BusinessException(
                    "End date must be after start date");
        }
        return promotionRepository.save(promotion);
    }

    public Promotion activatePromotion(Long id) {
        // Only ADMIN can activate promotions
        if (!SecurityUtil.hasRole(Role.ADMIN)) {
            throw new AccessDeniedException("Only ADMIN can activate promotions");
        }
        Promotion promotion = getPromotionById(id);
        promotion.setIsActive(true);
        return promotionRepository.save(promotion);
    }

    public Promotion deactivatePromotion(Long id) {
        // Only ADMIN can deactivate promotions
        if (!SecurityUtil.hasRole(Role.ADMIN)) {
            throw new AccessDeniedException("Only ADMIN can deactivate promotions");
        }
        Promotion promotion = getPromotionById(id);
        promotion.setIsActive(false);
        return promotionRepository.save(promotion);
    }

    public Promotion incrementUsage(Long id) {
        Promotion promotion = getPromotionById(id);
        if (promotion.getMaxUsage() != null && promotion.getCurrentUsage() >= promotion.getMaxUsage()) {
            throw new IllegalStateException("Promotion has reached maximum usage");
        }
        promotion.setCurrentUsage(promotion.getCurrentUsage() + 1);
        return promotionRepository.save(promotion);
    }

    public void deletePromotion(Long id) {
        // Only ADMIN can delete promotions
        if (!SecurityUtil.hasRole(Role.ADMIN)) {
            throw new AccessDeniedException("Only ADMIN can delete promotions");
        }
        try {
            log.info("Attempting to hard delete promotion with id: {}", id);
            Promotion promotion = getPromotionById(id);
            promotionRepository.delete(promotion);
            log.info("Successfully hard deleted promotion with id: {}", id);
        } catch (Exception e) {
            log.error("Error deleting promotion with id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public ValidationResultDTO validatePromotion(Long promotionId, BigDecimal montantCommande, Long userId) {
        Promotion promotion = getPromotionById(promotionId);

        if (!promotion.getIsActive()) {
            return ValidationResultDTO.builder().valide(false).message("Promotion inactive").build();
        }

        LocalDateTime now = LocalDateTime.now();
        if ((promotion.getStartDate() != null && now.isBefore(promotion.getStartDate())) ||
                (promotion.getEndDate() != null && now.isAfter(promotion.getEndDate()))) {
            return ValidationResultDTO.builder().valide(false).message("En dehors de la période de validité").build();
        }

        if (promotion.getMaxUsage() != null && promotion.getCurrentUsage() >= promotion.getMaxUsage()) {
            return ValidationResultDTO.builder().valide(false).message("Limite d'utilisation atteinte").build();
        }

        if (promotion.getMinPurchaseAmount() != null
                && montantCommande.compareTo(promotion.getMinPurchaseAmount()) < 0) {
            return ValidationResultDTO.builder().valide(false).message("Montant minimum non atteint").build();
        }

        if (userId != null && promotion.getTargetAudience() != null && !promotion.getTargetAudience().isEmpty()
                && !promotion.getTargetAudience().equals("ALL")) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                if (promotion.getTargetAudience().equals("VIP")
                        && (user.getLoyaltyTier() == null || !user.getLoyaltyTier().equals("VIP"))) {
                    return ValidationResultDTO.builder().valide(false)
                            .message("Cette promotion est réservée aux membres VIP").build();
                }
                if (promotion.getTargetAudience().equals("NEW_USERS") && user.getOrders() != null
                        && !user.getOrders().isEmpty()) {
                    return ValidationResultDTO.builder().valide(false)
                            .message("Cette promotion est réservée aux nouveaux utilisateurs").build();
                }
            }
        }

        BigDecimal reduction = BigDecimal.ZERO;

        if (promotion.getType() == PromotionType.PERCENTAGE) {
            reduction = montantCommande
                    .multiply(promotion.getDiscountValue().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
        } else if (promotion.getType() == PromotionType.FIXED_AMOUNT) {
            reduction = promotion.getDiscountValue();
        }

        if (promotion.getMaxDiscountAmount() != null && reduction.compareTo(promotion.getMaxDiscountAmount()) > 0) {
            reduction = promotion.getMaxDiscountAmount();
        }

        if (reduction.compareTo(montantCommande) > 0) {
            reduction = montantCommande;
        }

        BigDecimal montantFinal = montantCommande.subtract(reduction);

        return ValidationResultDTO.builder()
                .valide(true)
                .montantFinal(montantFinal)
                .reduction(reduction)
                .message("Promotion appliquée avec succès")
                .build();
    }
}
