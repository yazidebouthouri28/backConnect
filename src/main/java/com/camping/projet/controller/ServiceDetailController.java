package com.camping.projet.controller;

import com.camping.projet.dto.request.ServiceDetailRequest;
import com.camping.projet.dto.response.ServiceDetailResponse;
import com.camping.projet.service.IServiceDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/service-details")
@RequiredArgsConstructor
public class ServiceDetailController {

    private final IServiceDetailService detailService;

    @PostMapping
    public ResponseEntity<ServiceDetailResponse> saveOrUpdate(@Valid @RequestBody ServiceDetailRequest request) {
        return ResponseEntity.ok(detailService.saveOrUpdateDetail(request));
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<ServiceDetailResponse> getByServiceId(@PathVariable Long serviceId) {
        return ResponseEntity.ok(detailService.getDetailByServiceId(serviceId));
    }

    @PostMapping("/{serviceId}/photos")
    public ResponseEntity<Void> addPhoto(@PathVariable Long serviceId, @RequestParam String url) {
        detailService.addPhoto(serviceId, url);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{serviceId}/reviews")
    public ResponseEntity<Void> addReview(
            @PathVariable Long serviceId,
            @RequestParam Long userId,
            @RequestParam Integer rating,
            @RequestParam String comment) {
        detailService.addReview(serviceId, userId, rating, comment);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{serviceId}/specifications")
    public ResponseEntity<Void> updateSpecs(@PathVariable Long serviceId, @RequestBody Map<String, Object> specs) {
        detailService.updateSpecifications(serviceId, specs);
        return ResponseEntity.ok().build();
    }
}
