package com.camping.projet.service;

import com.camping.projet.dto.request.ProtocoleUrgenceRequest;
import com.camping.projet.dto.response.ProtocoleUrgenceResponse;
import com.camping.projet.enums.TypeUrgence;
import java.util.List;

public interface IProtocoleUrgenceService {
    ProtocoleUrgenceResponse createProtocole(ProtocoleUrgenceRequest request);

    ProtocoleUrgenceResponse updateProtocole(Long id, ProtocoleUrgenceRequest request);

    void deleteProtocole(Long id);

    ProtocoleUrgenceResponse getProtocoleById(Long id);

    List<ProtocoleUrgenceResponse> getAllProtocoles();

    List<ProtocoleUrgenceResponse> getProtocolesByType(TypeUrgence type);
}
