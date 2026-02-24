package com.example.nadineback.services;

import com.example.nadineback.entities.Remboursement;
import com.example.nadineback.entities.Transaction;
import com.example.nadineback.repositories.RemboursementRepository;
import com.example.nadineback.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RemboursementService implements IRemboursementService {


    private final RemboursementRepository repository;
    private final TransactionRepository transactionRepository;

    @Override
    public Remboursement create(Remboursement remboursement) {

        remboursement.setDateRemboursement(LocalDateTime.now());

        Remboursement saved = repository.save(remboursement);

        // Marquer transaction comme remboursée
        Transaction transaction = transactionRepository
                .findById(remboursement.getTransactionId())
                .orElseThrow(() -> new RuntimeException("Transaction introuvable"));

        transaction.setEstRembourse(true);
        transactionRepository.save(transaction);

        return saved;
    }

    @Override
    public List<Remboursement> getAll() {
        return repository.findAll();
    }

    @Override
    public Remboursement getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Remboursement introuvable"));
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }
}
