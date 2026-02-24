package com.example.nadineback.services;

import com.example.nadineback.entities.Facture;
import com.example.nadineback.entities.Transaction;
import com.example.nadineback.repositories.FactureRepository;
import com.example.nadineback.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService implements ITransactionService {
    private final TransactionRepository repository;
    private final FactureRepository factureRepository;

    @Override
    public Transaction create(Transaction transaction) {

        transaction.setDateTransaction(LocalDateTime.now());
        transaction.setEstRembourse(false);

        Transaction saved = repository.save(transaction);

        // Création automatique facture
        Facture facture = Facture.builder()
                .transactionId(saved.getId())
                .montant(saved.getMontant())
                .dateEmission(LocalDateTime.now())
                .numeroFacture("FAC-" + UUID.randomUUID().toString().substring(0,8))
                .payee(true)
                .build();

        factureRepository.save(facture);

        return saved;
    }

    @Override
    public List<Transaction> getAll() {
        return repository.findAll();
    }

    @Override
    public Transaction getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction introuvable"));
    }

    @Override
    public Transaction update(String id, Transaction transaction) {
        Transaction existing = getById(id);

        existing.setStatut(transaction.getStatut());
        existing.setModePaiement(transaction.getModePaiement());

        return repository.save(existing);
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }
}
