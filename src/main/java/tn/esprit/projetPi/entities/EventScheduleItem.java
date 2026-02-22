package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventScheduleItem {
    String title;
    String description;
    LocalDateTime startTime;
    LocalDateTime endTime;
    String location;
    String speaker;
}
