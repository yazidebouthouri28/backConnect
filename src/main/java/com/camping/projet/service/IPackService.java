package com.camping.projet.service;

import com.camping.projet.dto.request.PackRequest;
import com.camping.projet.dto.response.PackResponse;
import java.util.List;

public interface IPackService {
    PackResponse createPack(PackRequest request);

    PackResponse updatePack(Long id, PackRequest request);

    void deletePack(Long id);

    PackResponse getPackById(Long id);

    List<PackResponse> getAllPacks();

    List<PackResponse> getActivePacks();

    List<PackResponse> getPacksByService(Long serviceId);

    void updatePackStatus(Long id, boolean active);
}
