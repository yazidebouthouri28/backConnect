package com.camping.projet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventServiceResponse {
    private Long id;
    private Long eventId;
    private Long serviceId;
    private String serviceName;
    private ServiceResponse service;
    private Integer requiredQuantity;
    private Integer acceptedQuantity;
}
