package tn.esprit.backconnect.entities;

import lombok.*;
import jakarta.persistence.*;
import java.util.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Sponsor extends User {

    private String content;
    private String slogan;
    private long numberOfStars;

    @OneToMany(mappedBy = "sponsor", cascade = CascadeType.ALL)
    private List<Sponsorship> sponsorships;
}