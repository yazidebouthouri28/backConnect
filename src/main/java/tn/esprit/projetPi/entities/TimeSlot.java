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
@Document(collection = "time_slots")
public class TimeSlot {

    @Id
    String id;

    LocalDateTime startTime;

    LocalDateTime endTime;

    AvailabilityStatus availabilityStatus;

    String calendarTitle;

    String calendarDescription;

    Double calendarBasePrice;

    Boolean calendarIsActive;

    String cancellationReason;

    LocalDateTime cancellationDate;

    String reservationId;

    List<String> serviceIds = new ArrayList<>();
}
