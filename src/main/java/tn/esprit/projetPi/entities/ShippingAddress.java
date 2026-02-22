package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShippingAddress {
    String id;
    String fullName;
    String phone;
    String email;
    String addressLine1;
    String addressLine2;
    String city;
    String state;
    String postalCode;
    String country;
    Boolean isDefault;
    String label; // HOME, WORK, OTHER
}
