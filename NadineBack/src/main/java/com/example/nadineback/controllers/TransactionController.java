package com.example.nadineback.controllers;

import com.example.nadineback.entities.Transaction;
import com.example.nadineback.services.ITransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final ITransactionService transactionService;

    @PostMapping
    public Transaction create(@RequestBody Transaction transaction) {
        return transactionService.create(transaction);
    }

    @GetMapping
    public List<Transaction> getAll() {
        return transactionService.getAll();
    }

    @GetMapping("/{id}")
    public Transaction getById(@PathVariable String id) {
        return transactionService.getById(id);
    }

    @PutMapping("/{id}")
    public Transaction update(@PathVariable String id,
                              @RequestBody Transaction transaction) {
        return transactionService.update(id, transaction);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        transactionService.delete(id);
    }
}
