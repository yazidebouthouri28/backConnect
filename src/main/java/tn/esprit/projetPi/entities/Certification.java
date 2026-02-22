package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "certifications")
public class Certification {

    @Id
    String id;

    String siteId; // Référence à Site

    CertificationStatus status;

    Integer score;

    LocalDate issueDate;

    LocalDate expiryDate;

    List<CertificationItem> certificationItems = new ArrayList<>();
}
