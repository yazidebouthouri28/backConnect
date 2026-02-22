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
@Document(collection = "notifications")
public class Notification {

    @Id
    String id;  // MongoDB ObjectId

    String message;
    Boolean isRead = false;
    LocalDateTime sendDate;

    // Références vers d'autres documents
    @DBRef
    User recipient;  // Destinataire de la notification

    @DBRef
    Event event;  // Événement lié (facultatif)
}
