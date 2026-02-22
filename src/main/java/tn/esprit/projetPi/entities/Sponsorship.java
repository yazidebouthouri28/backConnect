package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "sponsorships")
public class Sponsorship {

    @Id
    String id;  // MongoDB ObjectId

    Double amount;
    LocalDateTime endDate;

    @DBRef
    Sponsor sponsor;  // Référence au sponsor
}
