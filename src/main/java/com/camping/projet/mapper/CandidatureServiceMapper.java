package com.camping.projet.mapper;

import com.camping.projet.entity.CandidatureService;
import com.camping.projet.dto.request.CandidatureServiceRequest;
import com.camping.projet.dto.response.CandidatureServiceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = { UserMapper.class,
        EventServiceMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CandidatureServiceMapper {

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "eventService.id", source = "eventServiceId")
    CandidatureService toEntity(CandidatureServiceRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.fullName")
    @Mapping(target = "eventServiceId", source = "eventService.id")
    @Mapping(target = "serviceName", source = "eventService.service.name")
    @Mapping(target = "eventId", source = "eventService.eventId")
    CandidatureServiceResponse toResponse(CandidatureService entity);
}
