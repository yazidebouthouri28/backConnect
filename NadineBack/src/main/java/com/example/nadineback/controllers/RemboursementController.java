package com.example.nadineback.controllers;

import com.example.nadineback.entities.Remboursement;
import com.example.nadineback.services.IRemboursementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/remboursements")
@RequiredArgsConstructor
public class RemboursementController {

    private final IRemboursementService remboursementService;

    @PostMapping
    public Remboursement create(@RequestBody Remboursement remboursement) {
        return remboursementService.create(remboursement);
    }

    @GetMapping
    public List<Remboursement> getAll() {
        return remboursementService.getAll();
    }

    @GetMapping("/{id}")
    public Remboursement getById(@PathVariable String id) {
        return remboursementService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        remboursementService.delete(id);
    }
}