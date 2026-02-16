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
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @Column(name = "reservation_date")
    private LocalDateTime reservationDate;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @OneToOne
    @JoinColumn(name = "timeSlotId")
    private TimeSlot timeSlot;

    @ManyToMany
    @JoinTable(
            name = "reservation_services",
            joinColumns = @JoinColumn(name = "reservation_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Service> selectedServices;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<ReservationHistory> history;
}