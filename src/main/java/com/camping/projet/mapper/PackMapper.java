package com.camping.projet.mapper;

import com.camping.projet.entity.Pack;
import com.camping.projet.dto.request.PackRequest;
import com.camping.projet.dto.response.PackResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = { ServiceMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PackMapper {

    Pack toEntity(PackRequest request);

    PackResponse toResponse(Pack pack);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "services", ignore = true)
    void updateEntity(PackRequest request, @MappingTarget Pack pack);
}
