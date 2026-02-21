package com.camping.projet.controller;

import com.camping.projet.dto.request.PackRequest;
import com.camping.projet.dto.response.PackResponse;
import com.camping.projet.service.IPackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/packs")
@RequiredArgsConstructor
public class PackController {

    private final IPackService packService;

    @PostMapping
    public ResponseEntity<PackResponse> createPack(@Valid @RequestBody PackRequest request) {
        return new ResponseEntity<>(packService.createPack(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PackResponse> updatePack(@PathVariable Long id, @Valid @RequestBody PackRequest request) {
        return ResponseEntity.ok(packService.updatePack(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePack(@PathVariable Long id) {
        packService.deletePack(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PackResponse> getPackById(@PathVariable Long id) {
        return ResponseEntity.ok(packService.getPackById(id));
    }

    @GetMapping
    public ResponseEntity<List<PackResponse>> getAllPacks() {
        return ResponseEntity.ok(packService.getAllPacks());
    }

    @GetMapping("/active")
    public ResponseEntity<List<PackResponse>> getActivePacks() {
        return ResponseEntity.ok(packService.getActivePacks());
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<PackResponse>> getPacksByService(@PathVariable Long serviceId) {
        return ResponseEntity.ok(packService.getPacksByService(serviceId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updatePackStatus(@PathVariable Long id, @RequestParam boolean active) {
        packService.updatePackStatus(id, active);
        return ResponseEntity.ok().build();
    }
}
