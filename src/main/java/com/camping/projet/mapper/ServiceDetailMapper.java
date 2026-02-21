package com.camping.projet.mapper;

import com.camping.projet.entity.mongo.ServiceDetail;
import com.camping.projet.dto.request.ServiceDetailRequest;
import com.camping.projet.dto.response.ServiceDetailResponse;
import com.camping.projet.dto.response.ReviewResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ServiceDetailMapper {

    ServiceDetail toEntity(ServiceDetailRequest request);

    ServiceDetailResponse toResponse(ServiceDetail detail);

    ReviewResponse mapReview(ServiceDetail.Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "serviceId", ignore = true)
    void updateEntity(ServiceDetailRequest request, @MappingTarget ServiceDetail detail);
}
