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
@Table(name = "user_achievements")
public class UserAchievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unlocked_date")
    private LocalDateTime unlockedDate;

    @Column(name = "is_displayed")
    private Boolean isDisplayed = false;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @ManyToOne
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement;
}
