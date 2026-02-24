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
@Table(name = "organizers")
public class Organizer extends User {

    @JsonIgnore
    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL)
    private List<Event> events;
    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "organizer_services", joinColumns = @JoinColumn(name = "organizer_id"), inverseJoinColumns = @JoinColumn(name = "service_id"))
    private List<Service> selectedServices;

    // Getters, Setters
}