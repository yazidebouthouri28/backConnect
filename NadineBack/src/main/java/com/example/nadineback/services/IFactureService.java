package com.example.nadineback.services;

import com.example.nadineback.entities.Facture;

import java.util.List;

public interface IFactureService {
    List<Facture> getAll();

    Facture getById(String id);

    void delete(String id);
}
