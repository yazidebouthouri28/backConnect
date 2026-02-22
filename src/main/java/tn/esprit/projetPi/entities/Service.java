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
@Document(collection = "services")
public class Service {

    @Id
    String id;

    String name;

    String description;

    Double basePrice;

    Boolean isActive;

    LocalDateTime serviceTime;

    List<String> organizerIds = new ArrayList<>();
}
