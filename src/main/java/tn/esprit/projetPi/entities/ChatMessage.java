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
@Document(collection = "chat_messages")
public class ChatMessage {

    @Id
    String id;  // MongoDB génère automatiquement un ID sous forme de String

    String content;
    LocalDateTime createdDate;

    // Utiliser DBRef pour référencer d'autres documents MongoDB
    @DBRef
    ChatRoom chatRoom;

    @DBRef
    User sender;
}
