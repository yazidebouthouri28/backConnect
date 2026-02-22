package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketTier {
    String name;
    String description;
    Double price;
    Integer quantity;
    Integer sold;
    Boolean isAvailable;
}
