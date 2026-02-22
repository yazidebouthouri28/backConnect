package tn.esprit.projetPi.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetPi.dto.ApiResponse;
import tn.esprit.projetPi.dto.InventoryDTO;
import tn.esprit.projetPi.dto.StockMovementDTO;
import tn.esprit.projetPi.services.InventoryService;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventoryDTO>>> getAllInventory() {
        List<InventoryDTO> inventory = inventoryService.getAllInventory();
        return ResponseEntity.ok(ApiResponse.success(inventory));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryDTO>> getInventoryById(@PathVariable String id) {
        InventoryDTO inventory = inventoryService.getInventoryById(id);
        return ResponseEntity.ok(ApiResponse.success(inventory));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<InventoryDTO>>> getInventoryByProduct(@PathVariable String productId) {
        List<InventoryDTO> inventory = inventoryService.getInventoryByProduct(productId);
        return ResponseEntity.ok(ApiResponse.success(inventory));
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<ApiResponse<List<InventoryDTO>>> getInventoryByWarehouse(@PathVariable String warehouseId) {
        List<InventoryDTO> inventory = inventoryService.getInventoryByWarehouse(warehouseId);
        return ResponseEntity.ok(ApiResponse.success(inventory));
    }

    @GetMapping("/alerts")
    public ResponseEntity<ApiResponse<List<InventoryDTO>>> getLowStockAlerts() {
        List<InventoryDTO> alerts = inventoryService.getLowStockAlerts();
        return ResponseEntity.ok(ApiResponse.success(alerts));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InventoryDTO>> createInventory(@Valid @RequestBody InventoryDTO inventoryDTO) {
        InventoryDTO created = inventoryService.createInventory(inventoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Inventory record created successfully", created));
    }

    @PostMapping("/{id}/movement")
    public ResponseEntity<ApiResponse<InventoryDTO>> updateStock(
            @PathVariable String id,
            @Valid @RequestBody StockMovementDTO movementDTO) {
        InventoryDTO updated = inventoryService.updateStock(id, movementDTO);
        return ResponseEntity.ok(ApiResponse.success("Stock updated successfully", updated));
    }

    @GetMapping("/{id}/movements")
    public ResponseEntity<ApiResponse<List<StockMovementDTO>>> getStockMovements(@PathVariable String id) {
        List<StockMovementDTO> movements = inventoryService.getStockMovements(id);
        return ResponseEntity.ok(ApiResponse.success(movements));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteInventory(@PathVariable String id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.ok(ApiResponse.success("Inventory record deleted successfully", null));
    }
}
