package com.camping.projet.repository;

import com.camping.projet.entity.UtilisationPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UtilisationPromotionRepository extends JpaRepository<UtilisationPromotion, Long> {

    @Query("SELECT up FROM UtilisationPromotion up WHERE up.user.id = :userId")
    List<UtilisationPromotion> findByUserId(@Param("userId") Long userId);

    @Query("SELECT up FROM UtilisationPromotion up WHERE up.promotion.id = :promotionId")
    List<UtilisationPromotion> findByPromotionId(@Param("promotionId") Long promotionId);

    @Query("SELECT up FROM UtilisationPromotion up WHERE up.user.id = :userId AND up.promotion.id = :promotionId")
    List<UtilisationPromotion> findByUserAndPromotion(@Param("userId") Long userId,
            @Param("promotionId") Long promotionId);
}
