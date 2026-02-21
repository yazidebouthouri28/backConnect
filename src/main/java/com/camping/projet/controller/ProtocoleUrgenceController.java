package com.camping.projet.controller;

import com.camping.projet.dto.request.ProtocoleUrgenceRequest;
import com.camping.projet.dto.response.ProtocoleUrgenceResponse;
import com.camping.projet.enums.TypeUrgence;
import com.camping.projet.service.IProtocoleUrgenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/protocoles")
@RequiredArgsConstructor
public class ProtocoleUrgenceController {

    private final IProtocoleUrgenceService protocoleService;

    @PostMapping
    public ResponseEntity<ProtocoleUrgenceResponse> create(@Valid @RequestBody ProtocoleUrgenceRequest request) {
        return new ResponseEntity<>(protocoleService.createProtocole(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProtocoleUrgenceResponse> update(@PathVariable Long id,
            @Valid @RequestBody ProtocoleUrgenceRequest request) {
        return ResponseEntity.ok(protocoleService.updateProtocole(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        protocoleService.deleteProtocole(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProtocoleUrgenceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(protocoleService.getProtocoleById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProtocoleUrgenceResponse>> getAll() {
        return ResponseEntity.ok(protocoleService.getAllProtocoles());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<ProtocoleUrgenceResponse>> getByType(@PathVariable TypeUrgence type) {
        return ResponseEntity.ok(protocoleService.getProtocolesByType(type));
    }
}
