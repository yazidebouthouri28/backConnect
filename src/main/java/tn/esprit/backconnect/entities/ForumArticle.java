package tn.esprit.backconnect.entities;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ForumArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long forumArticleId;
    private String title;
    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Many articles → One user
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    // One article → Many comments
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private List<ForumComment> comments;
}
