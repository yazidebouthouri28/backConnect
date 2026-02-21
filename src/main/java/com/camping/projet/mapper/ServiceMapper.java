package com.camping.projet.mapper;

import com.camping.projet.entity.Service;
import com.camping.projet.dto.request.ServiceRequest;
import com.camping.projet.dto.response.ServiceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ServiceMapper {

    Service toEntity(ServiceRequest request);

    ServiceResponse toResponse(Service service);

    @Mapping(target = "id", ignore = true)
    void updateEntity(ServiceRequest request, @MappingTarget Service service);
}
