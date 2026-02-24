package tn.esprit.backconnect.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import jakarta.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long userId;
    private Long age;

    private String username;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String phoneNumber;
    private String address;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<ForumArticle> articles;

    @JsonIgnore
    @OneToMany(mappedBy = "sender")
    private List<ChatMessage> messages;
}