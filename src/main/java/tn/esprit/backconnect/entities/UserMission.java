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
@Table(name = "user_missions")
public class UserMission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "progress")
    private Integer progress = 0;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Column(name = "started_date")
    private LocalDateTime startedDate;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @ManyToOne
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;
}
