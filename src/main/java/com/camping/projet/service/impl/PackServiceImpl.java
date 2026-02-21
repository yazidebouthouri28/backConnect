package com.camping.projet.service.impl;

import com.camping.projet.dto.request.PackRequest;
import com.camping.projet.dto.response.PackResponse;
import com.camping.projet.entity.Pack;
import com.camping.projet.entity.Service;
import com.camping.projet.mapper.PackMapper;
import com.camping.projet.repository.PackRepository;
import com.camping.projet.repository.ServiceRepository;
import com.camping.projet.service.IPackService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class PackServiceImpl implements IPackService {

    private final PackRepository packRepository;
    private final ServiceRepository serviceRepository;
    private final PackMapper packMapper;
    private final MessageSource messageSource;

    @Override
    @Transactional
    public PackResponse createPack(PackRequest request) {
        Pack pack = packMapper.toEntity(request);
        List<Service> services = serviceRepository.findAllById(request.getServiceIds());
        pack.setServices(new HashSet<>(services));

        calculateSavings(pack, services);

        return packMapper.toResponse(packRepository.save(pack));
    }

    @Override
    @Transactional
    public PackResponse updatePack(Long id, PackRequest request) {
        Pack pack = packRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        messageSource.getMessage("pack.error.notfound", null, LocaleContextHolder.getLocale())));

        packMapper.updateEntity(request, pack);

        if (request.getServiceIds() != null) {
            List<Service> services = serviceRepository.findAllById(request.getServiceIds());
            pack.setServices(new HashSet<>(services));
            calculateSavings(pack, services);
        }

        return packMapper.toResponse(packRepository.save(pack));
    }

    private void calculateSavings(Pack pack, List<Service> services) {
        BigDecimal totalOriginalPrice = services.stream()
                .map(Service::getPrice)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalOriginalPrice.compareTo(BigDecimal.ZERO) > 0 && pack.getPackPrice() != null) {
            BigDecimal savings = totalOriginalPrice.subtract(pack.getPackPrice());
            BigDecimal percentage = savings.multiply(new BigDecimal("100"))
                    .divide(totalOriginalPrice, 2, RoundingMode.HALF_UP);
            pack.setSavingsPercentage(percentage);
        } else {
            pack.setSavingsPercentage(BigDecimal.ZERO);
        }
    }

    @Override
    @Transactional
    public void deletePack(Long id) {
        packRepository.deleteById(id);
    }

    @Override
    public PackResponse getPackById(Long id) {
        return packRepository.findById(id)
                .map(packMapper::toResponse)
                .orElseThrow(() -> new RuntimeException(
                        messageSource.getMessage("pack.error.notfound", null, LocaleContextHolder.getLocale())));
    }

    @Override
    public List<PackResponse> getAllPacks() {
        return packRepository.findAll().stream()
                .map(packMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PackResponse> getActivePacks() {
        return packRepository.findActivePacks(LocalDate.now()).stream()
                .map(packMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PackResponse> getPacksByService(Long serviceId) {
        return packRepository.findPacksByServiceId(serviceId).stream()
                .map(packMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updatePackStatus(Long id, boolean active) {
        Pack pack = packRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        messageSource.getMessage("pack.error.notfound", null, LocaleContextHolder.getLocale())));
        pack.setActive(active);
        packRepository.save(pack);
    }
}
