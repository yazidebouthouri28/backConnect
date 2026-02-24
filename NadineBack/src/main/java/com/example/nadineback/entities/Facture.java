package com.example.nadineback.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "factures")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Facture {

    @Id
    private String id;

    private String transactionId;

    private Double montant;

    private LocalDateTime dateEmission;

    private String numeroFacture;

    private Boolean payee;
}