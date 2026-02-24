package com.example.nadineback.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "abonnements")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Abonnement {

    @Id
    private String id;

    private String userId;

    private String type;

    private Double prix;

    private LocalDateTime dateDebut;

    private LocalDateTime dateFin;

    private StatutAbonnement statut;

    private LocalDateTime createdAt;
}