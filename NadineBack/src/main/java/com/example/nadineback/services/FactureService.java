package com.example.nadineback.services;

import com.example.nadineback.entities.Facture;
import com.example.nadineback.repositories.FactureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FactureService implements IFactureService {

    private final FactureRepository repository;

    @Override
    public List<Facture> getAll() {
        return repository.findAll();
    }

    @Override
    public Facture getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture introuvable"));
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }
}
