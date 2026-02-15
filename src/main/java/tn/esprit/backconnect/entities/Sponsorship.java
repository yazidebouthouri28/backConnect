package tn.esprit.backconnect.entities;


import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Sponsorship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Sponsorshipid;

    private Double amount;
    private LocalDateTime endDate;

    @ManyToOne
    @JoinColumn(name = "sponsorId")
    private Sponsor sponsor;
}
