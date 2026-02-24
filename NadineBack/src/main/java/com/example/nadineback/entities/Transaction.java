package com.example.nadineback.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "transactions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    private String id;

    private String abonnementId;

    private Double montant;

    private StatutTransaction statut;

    private ModePaiement modePaiement;

    private LocalDateTime dateTransaction;

    private Boolean estRembourse;
}