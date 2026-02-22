package tn.esprit.projetPi.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetPi.dto.ApiResponse;
import tn.esprit.projetPi.dto.WarehouseDTO;
import tn.esprit.projetPi.services.WarehouseService;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<WarehouseDTO>>> getAllWarehouses() {
        List<WarehouseDTO> warehouses = warehouseService.getAllWarehouses();
        return ResponseEntity.ok(ApiResponse.success(warehouses));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<WarehouseDTO>>> getActiveWarehouses() {
        List<WarehouseDTO> warehouses = warehouseService.getActiveWarehouses();
        return ResponseEntity.ok(ApiResponse.success(warehouses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WarehouseDTO>> getWarehouseById(@PathVariable String id) {
        WarehouseDTO warehouse = warehouseService.getWarehouseById(id);
        return ResponseEntity.ok(ApiResponse.success(warehouse));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<WarehouseDTO>> getWarehouseByCode(@PathVariable String code) {
        WarehouseDTO warehouse = warehouseService.getWarehouseByCode(code);
        return ResponseEntity.ok(ApiResponse.success(warehouse));
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<ApiResponse<List<WarehouseDTO>>> getWarehousesByCity(@PathVariable String city) {
        List<WarehouseDTO> warehouses = warehouseService.getWarehousesByCity(city);
        return ResponseEntity.ok(ApiResponse.success(warehouses));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WarehouseDTO>> createWarehouse(@Valid @RequestBody WarehouseDTO warehouseDTO) {
        WarehouseDTO created = warehouseService.createWarehouse(warehouseDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Warehouse created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WarehouseDTO>> updateWarehouse(
            @PathVariable String id,
            @Valid @RequestBody WarehouseDTO warehouseDTO) {
        WarehouseDTO updated = warehouseService.updateWarehouse(id, warehouseDTO);
        return ResponseEntity.ok(ApiResponse.success("Warehouse updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWarehouse(@PathVariable String id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.ok(ApiResponse.success("Warehouse deleted successfully", null));
    }
}
