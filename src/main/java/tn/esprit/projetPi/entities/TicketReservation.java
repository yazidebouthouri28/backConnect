package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "ticket_reservations")
public class TicketReservation {

    @Id
    String id;

    LocalDateTime reservationDate;

    Integer quantity;

    ReservationStatus status;

    String participantId;

    String eventId;

    List<String> ticketIds = new ArrayList<>();
}
