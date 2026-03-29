package com.example.nadineback.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "remboursements")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Remboursement {

    @Id
    private String id;

    private String transactionId;

    private Double montant;

    private String raison;

    private LocalDateTime dateRemboursement;

    private StatutRemboursement statut;
}