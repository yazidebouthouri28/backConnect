package tn.esprit.backconnect.entities;


import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Organizer extends User {

    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL)
    private List<Event> events;
    @ManyToMany
    @JoinTable(
            name = "organizer_services",
            joinColumns = @JoinColumn(name = "organizer_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Service> selectedServices;


    // Getters, Setters
}