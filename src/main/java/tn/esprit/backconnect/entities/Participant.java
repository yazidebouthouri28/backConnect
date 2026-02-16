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
@Table(name = "participants")
public class Participant extends User {

    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL)
    private List<TicketReservation> reservations;

    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL)
    private List<EventComment> comments;

    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL)
    private List<Complaint> complaints;

    // Getters, Setters
}
