package com.camping.projet.mapper;

import com.camping.projet.entity.UtilisationPromotion;
import com.camping.projet.dto.response.UtilisationPromotionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = { UserMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UtilisationPromotionMapper {

    @Mapping(target = "promotionId", source = "promotion.id")
    @Mapping(target = "reservationId", source = "reservationId")
    UtilisationPromotionResponse toResponse(UtilisationPromotion entity);
}
