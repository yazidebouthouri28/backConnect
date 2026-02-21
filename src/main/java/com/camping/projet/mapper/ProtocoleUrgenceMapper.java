package com.camping.projet.mapper;

import com.camping.projet.entity.ProtocoleUrgence;
import com.camping.projet.dto.request.ProtocoleUrgenceRequest;
import com.camping.projet.dto.response.ProtocoleUrgenceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProtocoleUrgenceMapper {

    ProtocoleUrgence toEntity(ProtocoleUrgenceRequest request);

    ProtocoleUrgenceResponse toResponse(ProtocoleUrgence entity);

    @Mapping(target = "id", ignore = true)
    void updateEntity(ProtocoleUrgenceRequest request, @MappingTarget ProtocoleUrgence entity);
}
