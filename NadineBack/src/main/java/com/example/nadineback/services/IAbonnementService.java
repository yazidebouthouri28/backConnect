package com.example.nadineback.services;

import com.example.nadineback.entities.Abonnement;

import java.util.List;

public interface IAbonnementService {
    Abonnement create(Abonnement abonnement);

    List<Abonnement> getAll();

    Abonnement getById(String id);

    Abonnement update(String id, Abonnement abonnement);

    void delete(String id);
}
