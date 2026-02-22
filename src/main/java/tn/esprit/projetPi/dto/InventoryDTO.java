package tn.esprit.projetPi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InventoryDTO {
    private String id;
    
    @NotBlank(message = "Product ID is required")
    private String productId;
    private String productName;
    private String sku;
    
    @NotBlank(message = "Warehouse ID is required")
    private String warehouseId;
    private String warehouseName;
    
    private String locationCode;
    
    @NotNull(message = "Current stock is required")
    private Integer currentStock;
    private Integer reservedStock;
    private Integer availableStock;
    
    private Integer lowStockThreshold;
    private Boolean isLowStock;
    
    private LocalDateTime lastRestockedAt;
}
