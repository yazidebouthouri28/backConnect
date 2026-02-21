package com.camping.projet.service;

import com.camping.projet.dto.request.ServiceRequest;
import com.camping.projet.dto.response.ServiceResponse;
import com.camping.projet.enums.ServiceType;
import java.util.List;

public interface IServiceService {
    ServiceResponse createService(ServiceRequest request);

    ServiceResponse updateService(Long id, ServiceRequest request);

    void deleteService(Long id);

    ServiceResponse getServiceById(Long id);

    List<ServiceResponse> getAllServices();

    List<ServiceResponse> getServicesByCamping(Long campingId);

    List<ServiceResponse> getServicesByServiceType(ServiceType serviceType);

    void updateAvailability(Long id, boolean disponible);
}
