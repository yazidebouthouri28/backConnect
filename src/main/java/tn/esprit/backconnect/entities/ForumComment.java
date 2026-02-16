package tn.esprit.backconnect.entities;


import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ForumComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long forumCommentId;

    private String title;
    private String content;
    private LocalDateTime createdAt;

    // Many comments → One user

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId")
    private User user;

    // Many comments → One article
    private long articleId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "forumId")
    private ForumArticle article;
}
