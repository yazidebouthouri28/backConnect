package com.camping.projet.mapper;

import com.camping.projet.entity.ConditionPromotion;
import com.camping.projet.dto.request.ConditionPromotionRequest;
import com.camping.projet.dto.response.ConditionPromotionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConditionPromotionMapper {

    @Mapping(target = "promotion.id", source = "promotionId")
    ConditionPromotion toEntity(ConditionPromotionRequest request);

    @Mapping(target = "promotionId", source = "promotion.id")
    ConditionPromotionResponse toResponse(ConditionPromotion condition);
}
