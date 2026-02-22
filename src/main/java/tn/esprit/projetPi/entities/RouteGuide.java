package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "route_guides")
public class RouteGuide {

    @Id
    String id;  // MongoDB ObjectId

    @DBRef
    Site site;  // Site associé au guide

    String originCity;
    Double distanceKm;
    Integer durationMin;
    String instructions; // JSON format for steps
    String mapUrl;
}
