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
@Table(name = "services")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;

    private String name;
    private String description;
    private Double basePrice;
    private Boolean isActive;

    private LocalDateTime serviceTime;

    @ManyToMany(mappedBy = "selectedServices")
    private List<Organizer> organisateurs;

    // Getters and setters
}