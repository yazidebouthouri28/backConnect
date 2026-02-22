package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "event_comments")
public class EventComment {

    @Id
    String id;  // MongoDB génère automatiquement un ObjectId

    String content;
    Integer rating; // 1-5
    LocalDateTime publicationDate;

    // Relations vers d'autres documents
    @DBRef
    Participant participant; // Auteur du commentaire

    @DBRef
    Event event; // Événement concerné
}
