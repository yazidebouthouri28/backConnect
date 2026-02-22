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
@Document(collection = "forum_comments")
public class ForumComment {

    @Id
    String id;  // MongoDB ObjectId

    String title;
    String content;
    LocalDateTime createdAt;

    // Référence vers l'auteur
    @DBRef
    User user;

    // Référence vers l'article parent
    @DBRef
    ForumArticle article;
}
