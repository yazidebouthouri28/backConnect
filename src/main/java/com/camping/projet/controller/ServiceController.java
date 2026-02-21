package com.camping.projet.controller;

import com.camping.projet.dto.request.ServiceRequest;
import com.camping.projet.dto.response.ServiceResponse;
import com.camping.projet.enums.ServiceType;
import com.camping.projet.service.IServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final IServiceService serviceService;

    @PostMapping
    public ResponseEntity<ServiceResponse> createService(@Valid @RequestBody ServiceRequest request) {
        return new ResponseEntity<>(serviceService.createService(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateService(@PathVariable Long id,
            @Valid @RequestBody ServiceRequest request) {
        return ResponseEntity.ok(serviceService.updateService(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceService.getServiceById(id));
    }

    @GetMapping
    public ResponseEntity<List<ServiceResponse>> getAllServices() {
        return ResponseEntity.ok(serviceService.getAllServices());
    }

    @GetMapping("/camping/{campingId}")
    public ResponseEntity<List<ServiceResponse>> getServicesByCamping(@PathVariable Long campingId) {
        return ResponseEntity.ok(serviceService.getServicesByCamping(campingId));
    }

    @GetMapping("/type/{serviceType}")
    public ResponseEntity<List<ServiceResponse>> getServicesByServiceType(@PathVariable ServiceType serviceType) {
        return ResponseEntity.ok(serviceService.getServicesByServiceType(serviceType));
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<Void> updateAvailability(@PathVariable Long id, @RequestParam boolean available) {
        serviceService.updateAvailability(id, available);
        return ResponseEntity.ok().build();
    }
}
