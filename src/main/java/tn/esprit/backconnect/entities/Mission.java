package tn.esprit.backconnect.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import jakarta.persistence.*;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "missions")
public class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "xp_reward", nullable = false)
    private Integer xpReward;

    @Enumerated(EnumType.STRING)
    @Column(name = "mission_type", nullable = false)
    private MissionType missionType;

    @Column(name = "target_count")
    private Integer targetCount;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @JsonIgnore
    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL)
    private List<UserMission> userMissions;
}
