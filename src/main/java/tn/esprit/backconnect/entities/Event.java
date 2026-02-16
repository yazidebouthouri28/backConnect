package tn.esprit.backconnect.entities;


import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private String location;

    @Column(name = "ticket_price")
    private long ticketPrice;

    @Enumerated(EnumType.STRING)
    private EventStatus status; // DRAFT, PUBLISHED, CANCELLED, COMPLETED

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    private Organizer organizer;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<TicketReservation> reservations;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Notification> notifications;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventComment> comments;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Complaint> complaints;

    // Getters, Setters, Constructors
}