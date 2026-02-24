package tn.esprit.backconnect.entities;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "achievements")
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "required_xp")
    private Integer requiredXp;

    @Enumerated(EnumType.STRING)
    @Column(name = "badge_level")
    private BadgeLevel badgeLevel;

    @OneToMany(mappedBy = "achievement", cascade = CascadeType.ALL)
    private List<UserAchievement> userAchievements;
}
