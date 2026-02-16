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
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "send_date")
    private LocalDateTime sendDate;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    // Getters, Setters
}