package com.camping.projet.mapper;

import com.camping.projet.entity.EventService;
import com.camping.projet.dto.request.EventServiceRequest;
import com.camping.projet.dto.response.EventServiceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = { ServiceMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventServiceMapper {

    @Mapping(target = "service.id", source = "serviceId")
    EventService toEntity(EventServiceRequest request);

    @Mapping(target = "serviceId", source = "service.id")
    @Mapping(target = "serviceName", source = "service.name")
    EventServiceResponse toResponse(EventService entity);
}
