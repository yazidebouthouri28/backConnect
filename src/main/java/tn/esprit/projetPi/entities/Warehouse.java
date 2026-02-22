package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "warehouses")
public class Warehouse {

    @Id
    String id;

    String name;
    String code;
    String address;
    String city;
    String country;
    String phone;
    String email;
    Boolean isActive;
}
