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
@Table(name = "ticket_requests")
public class TicketRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_date")
    private LocalDateTime requestDate;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private TicketRequestStatus status;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "response_date")
    private LocalDateTime responseDate;

    @Column(columnDefinition = "TEXT")
    private String responseMessage;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
}
