package com.example.nadineback.services;

import com.example.nadineback.entities.Transaction;

import java.util.List;

public interface ITransactionService {
    Transaction create(Transaction transaction);

    List<Transaction> getAll();

    Transaction getById(String id);

    Transaction update(String id, Transaction transaction);

    void delete(String id);
}
