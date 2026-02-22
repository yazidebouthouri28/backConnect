package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "sites")
public class Site {

    @Id
    String id;

    String name;

    String description;

    Double latitude;

    Double longitude;

    String address;

    String city;

    Double averageRating;

    String image;

    List<String> certificationIds = new ArrayList<>();
    List<String> virtualTourIds = new ArrayList<>();
    List<String> routeGuideIds = new ArrayList<>();
    List<String> reviewIds = new ArrayList<>();
}
