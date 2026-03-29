package com.example.nadineback.services;

import com.example.nadineback.entities.Remboursement;

import java.util.List;

public interface IRemboursementService {

    Remboursement create(Remboursement remboursement);

    List<Remboursement> getAll();

    Remboursement getById(String id);

    void delete(String id);
}
