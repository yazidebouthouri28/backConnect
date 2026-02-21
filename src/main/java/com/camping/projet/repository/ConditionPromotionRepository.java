package com.camping.projet.repository;

import com.camping.projet.entity.ConditionPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConditionPromotionRepository extends JpaRepository<ConditionPromotion, Long> {
    List<ConditionPromotion> findByPromotionId(Long promotionId);
}
