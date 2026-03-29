package com.example.nadineback.controllers;

import com.example.nadineback.entities.Facture;
import com.example.nadineback.services.IFactureService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/factures")
@RequiredArgsConstructor
public class FactureController {

    private final IFactureService factureService;

    @GetMapping
    public List<Facture> getAll() {
        return factureService.getAll();
    }

    @GetMapping("/{id}")
    public Facture getById(@PathVariable String id) {
        return factureService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        factureService.delete(id);
    }
}