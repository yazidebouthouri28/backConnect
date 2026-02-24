package com.example.nadineback.controllers;

import com.example.nadineback.entities.Abonnement;
import com.example.nadineback.services.IAbonnementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/abonnements")
@RequiredArgsConstructor
public class AbonnementController {
    private final IAbonnementService abonnementService;

    @PostMapping
    public Abonnement create(@RequestBody Abonnement abonnement) {
        return abonnementService.create(abonnement);
    }

    @GetMapping
    public List<Abonnement> getAll() {
        return abonnementService.getAll();
    }

    @GetMapping("/{id}")
    public Abonnement getById(@PathVariable String id) {
        return abonnementService.getById(id);
    }

    @PutMapping("/{id}")
    public Abonnement update(@PathVariable String id,
                             @RequestBody Abonnement abonnement) {
        return abonnementService.update(id, abonnement);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        abonnementService.delete(id);
    }
}
