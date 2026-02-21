package com.camping.projet.service.impl;

import com.camping.projet.dto.request.ServiceRequest;
import com.camping.projet.dto.response.ServiceResponse;
import com.camping.projet.entity.Service;
import com.camping.projet.enums.ServiceType;
import com.camping.projet.mapper.ServiceMapper;
import com.camping.projet.repository.ServiceRepository;
import com.camping.projet.service.IServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements IServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;
    private final MessageSource messageSource;

    @Override
    @Transactional
    public ServiceResponse createService(ServiceRequest request) {
        if (serviceRepository.existsByNameAndCampingId(request.getName(), request.getCampingId())) {
            throw new RuntimeException(
                    messageSource.getMessage("service.error.already.exists", null, LocaleContextHolder.getLocale()));
        }
        Service service = serviceMapper.toEntity(request);
        service.setAvailable(true);
        return serviceMapper.toResponse(serviceRepository.save(service));
    }

    @Override
    @Transactional
    public ServiceResponse updateService(Long id, ServiceRequest request) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        messageSource.getMessage("service.error.notfound", null, LocaleContextHolder.getLocale())));
        serviceMapper.updateEntity(request, service);
        return serviceMapper.toResponse(serviceRepository.save(service));
    }

    @Override
    @Transactional
    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }

    @Override
    public ServiceResponse getServiceById(Long id) {
        return serviceRepository.findById(id)
                .map(serviceMapper::toResponse)
                .orElseThrow(() -> new RuntimeException(
                        messageSource.getMessage("service.error.notfound", null, LocaleContextHolder.getLocale())));
    }

    @Override
    public List<ServiceResponse> getAllServices() {
        return serviceRepository.findAll().stream()
                .map(serviceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceResponse> getServicesByCamping(Long campingId) {
        return serviceRepository.findByCampingId(campingId).stream()
                .map(serviceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceResponse> getServicesByServiceType(ServiceType serviceType) {
        return serviceRepository.findByServiceType(serviceType).stream()
                .map(serviceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateAvailability(Long id, boolean available) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        messageSource.getMessage("service.error.notfound", null, LocaleContextHolder.getLocale())));
        service.setAvailable(available);
        serviceRepository.save(service);
    }
}
