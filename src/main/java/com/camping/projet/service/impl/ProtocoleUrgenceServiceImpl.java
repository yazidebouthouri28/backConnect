package com.camping.projet.service.impl;

import com.camping.projet.dto.request.ProtocoleUrgenceRequest;
import com.camping.projet.dto.response.ProtocoleUrgenceResponse;
import com.camping.projet.entity.ProtocoleUrgence;
import com.camping.projet.enums.TypeUrgence;
import com.camping.projet.mapper.ProtocoleUrgenceMapper;
import com.camping.projet.repository.ProtocoleUrgenceRepository;
import com.camping.projet.service.IProtocoleUrgenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProtocoleUrgenceServiceImpl implements IProtocoleUrgenceService {

    private final ProtocoleUrgenceRepository protocoleRepository;
    private final ProtocoleUrgenceMapper protocoleMapper;

    @Override
    public ProtocoleUrgenceResponse createProtocole(ProtocoleUrgenceRequest request) {
        ProtocoleUrgence protocole = protocoleMapper.toEntity(request);
        return protocoleMapper.toResponse(protocoleRepository.save(protocole));
    }

    @Override
    public ProtocoleUrgenceResponse updateProtocole(Long id, ProtocoleUrgenceRequest request) {
        ProtocoleUrgence protocole = protocoleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Protocole not found"));
        protocoleMapper.updateEntity(request, protocole);
        return protocoleMapper.toResponse(protocoleRepository.save(protocole));
    }

    @Override
    public void deleteProtocole(Long id) {
        protocoleRepository.deleteById(id);
    }

    @Override
    public ProtocoleUrgenceResponse getProtocoleById(Long id) {
        return protocoleRepository.findById(id)
                .map(protocoleMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Protocole not found"));
    }

    @Override
    public List<ProtocoleUrgenceResponse> getAllProtocoles() {
        return protocoleRepository.findAll().stream()
                .map(protocoleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProtocoleUrgenceResponse> getProtocolesByType(TypeUrgence type) {
        return protocoleRepository.findByType(type).stream()
                .map(protocoleMapper::toResponse)
                .collect(Collectors.toList());
    }
}
