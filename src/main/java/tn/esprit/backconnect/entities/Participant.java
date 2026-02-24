package tn.esprit.backconnect.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Participant extends User {

    // Gamification fields
    @Column(name = "xp_points")
    private Integer xpPoints = 0;

    @Column(name = "level")
    private Integer level = 1;

    @Column(name = "streak")
    private Integer streak = 0;

    @Column(name = "last_activity_date")
    private LocalDateTime lastActivityDate;

    @JsonIgnore
    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL)
    private List<TicketReservation> reservations;

    @JsonIgnore
    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL)
    private List<TicketRequest> ticketRequests;

    @JsonIgnore
    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL)
    private List<EventComment> comments;

    @JsonIgnore
    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL)
    private List<Complaint> complaints;

    @JsonIgnore
    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL)
    private List<UserMission> userMissions;

    @JsonIgnore
    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL)
    private List<UserAchievement> userAchievements;
}
