package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "seller_settings")
public class SellerSettings {

    @Id
    String id;

    Boolean stockAlerts;
    Boolean orderAlerts;
    Boolean rentalReminders;
    Boolean emailReports;

    Integer globalThreshold;
    Integer autoRestockThreshold;
    Integer defaultRentalDays;
    Integer defaultDepositPercent;

    BigDecimal lateFeePerDay;
}
