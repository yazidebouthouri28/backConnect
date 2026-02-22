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
@Document(collection = "stock_movements")
public class StockMovement {

    @Id
    String id;

    String inventoryId;
    String productId;
    String warehouseId;
    
    MovementType type;
    Integer quantity;
    Integer previousStock;
    Integer newStock;
    
    String reason;
    String reference;
    String performedBy;
    
    LocalDateTime createdAt;
}
