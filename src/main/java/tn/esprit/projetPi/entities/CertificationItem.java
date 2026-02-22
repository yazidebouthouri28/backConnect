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
@Document(collection = "certification_items")
public class CertificationItem {

    @Id
    String id;

    String certificationId; // référence à Certification.id

    CriterieName criteriaName;

    Integer score; // 0-10

    String comment;
}
