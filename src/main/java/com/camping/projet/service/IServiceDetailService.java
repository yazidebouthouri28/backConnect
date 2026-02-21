package com.camping.projet.service;

import com.camping.projet.dto.request.ServiceDetailRequest;
import com.camping.projet.dto.response.ServiceDetailResponse;
import java.util.Map;

public interface IServiceDetailService {
    ServiceDetailResponse saveOrUpdateDetail(ServiceDetailRequest request);

    ServiceDetailResponse getDetailByServiceId(Long serviceId);

    void addPhoto(Long serviceId, String photoUrl);

    void addReview(Long serviceId, Long userId, Integer rating, String comment);

    void updateSpecifications(Long serviceId, Map<String, Object> specs);
}
