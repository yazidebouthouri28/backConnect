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
@Document(collection = "inventory")
public class Inventory {

    @Id
    String id;

    String productId;
    String productName;
    String sku;

    String warehouseId;
    String warehouseName;

    String locationCode;

    Integer currentStock;
    Integer reservedStock;
    Integer availableStock;

    Integer lowStockThreshold;
    Boolean isLowStock;

    LocalDateTime lastRestockedAt;
}
