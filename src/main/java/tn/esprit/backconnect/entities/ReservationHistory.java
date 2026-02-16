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
public class ReservationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationHistoryId;

    private String action;
    private LocalDateTime actionDate;

    @ManyToOne
    @JoinColumn(name = "reservationId")
    private Reservation reservation;

    // Getters et setters
}