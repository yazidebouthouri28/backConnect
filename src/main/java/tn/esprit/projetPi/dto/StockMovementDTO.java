package tn.esprit.projetPi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tn.esprit.projetPi.entities.MovementType;
import java.time.LocalDateTime;

@Data
public class StockMovementDTO {
    private String id;
    
    @NotBlank(message = "Inventory ID is required")
    private String inventoryId;
    private String productId;
    private String warehouseId;
    
    @NotNull(message = "Movement type is required")
    private MovementType type;
    
    @NotNull(message = "Quantity is required")
    private Integer quantity;
    
    private Integer previousStock;
    private Integer newStock;
    private String reason;
    private String reference;
    private String performedBy;
    private LocalDateTime createdAt;
}
