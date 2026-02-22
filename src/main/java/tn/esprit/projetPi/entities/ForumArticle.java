package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "forum_articles")
public class ForumArticle {

    @Id
    String id;  // MongoDB ObjectId

    String title;
    String content;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    @DBRef
    User user;  // Auteur de l'article

    @DBRef
    List<ForumComment> comments;  // Références aux commentaires
}
