package com.camping.projet.mapper;

import com.camping.projet.entity.InterventionSecours;
import com.camping.projet.dto.request.InterventionSecoursRequest;
import com.camping.projet.dto.response.InterventionSecoursResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InterventionSecoursMapper {

    @Mapping(target = "alerte.id", source = "alerteId")
    InterventionSecours toEntity(InterventionSecoursRequest request);

    @Mapping(target = "alerteId", source = "alerte.id")
    InterventionSecoursResponse toResponse(InterventionSecours entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "alerte", ignore = true)
    @Mapping(target = "heureDepart", ignore = true)
    void updateEntity(InterventionSecoursRequest request, @MappingTarget InterventionSecours entity);
}
