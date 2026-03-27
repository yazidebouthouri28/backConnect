package tn.esprit.projetintegre.repositories.projections;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import tn.esprit.projetintegre.enums.PackType;

public interface PackProjection {
    Long getId();
    String getName();
    String getDescription();
    PackType getPackType();
    BigDecimal getPrice();
    BigDecimal getOriginalPrice();
    Integer getDurationDays();
    Integer getMaxPersons();
    Boolean getIsActive();
    Boolean getIsFeatured();
    Boolean getIsLimitedOffer();
    Integer getAvailableQuantity();
    Integer getSoldCount();
    Double getRating();
    Integer getReviewCount();
    LocalDateTime getValidFrom();
    LocalDateTime getValidUntil();
    LocalDateTime getCreatedAt();
    Integer getServiceCount();
    String getImageUrl();
    
    // Pour les relations, on peut utiliser des projections imbriquées ou charger les IDs
    Long getSiteId();
    String getSiteName();
}
