package com.camping.projet.mapper;

import com.camping.projet.entity.ExerciceEvacuation;
import com.camping.projet.dto.request.ExerciceEvacuationRequest;
import com.camping.projet.dto.response.ExerciceEvacuationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExerciceEvacuationMapper {

    ExerciceEvacuation toEntity(ExerciceEvacuationRequest request);

    ExerciceEvacuationResponse toResponse(ExerciceEvacuation entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reussi", ignore = true)
    void updateEntity(ExerciceEvacuationRequest request, @MappingTarget ExerciceEvacuation entity);
}
