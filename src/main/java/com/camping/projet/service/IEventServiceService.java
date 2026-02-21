package com.camping.projet.service;

import com.camping.projet.dto.request.EventServiceRequest;
import com.camping.projet.dto.response.EventServiceResponse;
import java.util.List;

public interface IEventServiceService {
    EventServiceResponse assignServiceToEvent(EventServiceRequest request);

    void removeServiceFromEvent(Long eventServiceId);

    List<EventServiceResponse> getServicesByEvent(Long eventId);

    List<EventServiceResponse> getAvailableEventServices();

    void updateRequiredQuantity(Long eventServiceId, Integer newQuantity);
}
