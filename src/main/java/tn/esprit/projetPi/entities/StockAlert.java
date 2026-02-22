package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "stock_alerts")
public class StockAlert {

    @Id
    String id;

    String productId;
    String productName;
    String sellerId;
    
    AlertType type; // LOW_STOCK, OUT_OF_STOCK, BACK_IN_STOCK
    Integer currentStock;
    Integer threshold;
    
    Boolean acknowledged;
    String acknowledgedBy;
    LocalDateTime acknowledgedAt;
    
    LocalDateTime createdAt;
}
