package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "complaints")
public class Complaint {

    @Id
    String id;

    String subject;

    String description;

    ComplaintStatus status;

    LocalDateTime creationDate;

    String participantId; // Référence à Participant

    String eventId; // Référence à Event
}
