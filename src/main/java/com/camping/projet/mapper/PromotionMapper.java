package com.camping.projet.mapper;

import com.camping.projet.entity.Promotion;
import com.camping.projet.dto.request.PromotionRequest;
import com.camping.projet.dto.response.PromotionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PromotionMapper {

    Promotion toEntity(PromotionRequest request);

    PromotionResponse toResponse(Promotion promotion);

    @Mapping(target = "id", ignore = true)
    void updateEntity(PromotionRequest request, @MappingTarget Promotion promotion);
}
