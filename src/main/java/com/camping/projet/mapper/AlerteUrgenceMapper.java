package com.camping.projet.mapper;

import com.camping.projet.entity.AlerteUrgence;
import com.camping.projet.dto.request.AlerteUrgenceRequest;
import com.camping.projet.dto.response.AlerteUrgenceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {
        ProtocoleUrgenceMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlerteUrgenceMapper {

    @Mapping(target = "protocole.id", source = "protocoleId")
    AlerteUrgence toEntity(AlerteUrgenceRequest request);

    @Mapping(target = "protocole", source = "protocole")
    @Mapping(target = "declencheurId", source = "declencheurId")
    AlerteUrgenceResponse toResponse(AlerteUrgence entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateHeure", ignore = true)
    @Mapping(target = "protocole", ignore = true)
    void updateEntity(AlerteUrgenceRequest request, @MappingTarget AlerteUrgence entity);
}
