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
public class TimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timeSlotId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private AvailabilityStatus availabilityStatus;  // AVAILABLE, RESERVED, CANCELLED

    // Calendar est un attribut (titre, description, prix de base)
    private String calendarTitle;
    private String calendarDescription;
    private Double calendarBasePrice;
    private Boolean calendarIsActive;

    // Cancellation est un attribut
    private String cancellationReason;
    private LocalDateTime cancellationDate;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToMany
    @JoinTable(
            name = "timeslot_services",
            joinColumns = @JoinColumn(name = "timeslot_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Service> services;

    // Getters et setters
}