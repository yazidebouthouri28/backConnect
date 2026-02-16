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
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @Column(name = "ticket_number", unique = true, nullable = false)
    private String ticketNumber;

    @Enumerated(EnumType.STRING)
    private TicketStatus status; // ACTIVE, USED, CANCELLED

    // Relationship
    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private TicketReservation reservation;

    // Getters, Setters
}