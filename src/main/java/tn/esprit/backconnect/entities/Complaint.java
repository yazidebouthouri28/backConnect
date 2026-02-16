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
@Table(name = "complaints")
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long complaintId;

    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus status; // OPEN, IN_PROGRESS, RESOLVED, CLOSED

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Participant participant;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    // Getters, Setters
}