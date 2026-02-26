package tn.esprit.projetintegre.entities;


import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class EventComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer rating; // 1-5

    @Column(name = "publication_date")
    private LocalDateTime publicationDate;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Participant participant;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    // Getters, Setters
}