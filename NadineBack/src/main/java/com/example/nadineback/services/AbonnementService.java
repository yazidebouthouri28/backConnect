package com.example.nadineback.services;

import com.example.nadineback.entities.Abonnement;
import com.example.nadineback.repositories.AbonnementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class AbonnementService implements IAbonnementService {
    private final AbonnementRepository repository;


    @Override
    public Abonnement create(Abonnement abonnement) {
        abonnement.setCreatedAt(LocalDateTime.now());
        return repository.save(abonnement);
    }

    @Override
    public List<Abonnement> getAll() {
        return repository.findAll();
    }

    @Override
    public Abonnement getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Abonnement introuvable"));
    }

    @Override
    public Abonnement update(String id, Abonnement abonnement) {
        Abonnement existing = getById(id);

        existing.setType(abonnement.getType());
        existing.setPrix(abonnement.getPrix());
        existing.setStatut(abonnement.getStatut());

        return repository.save(existing);
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }
}
