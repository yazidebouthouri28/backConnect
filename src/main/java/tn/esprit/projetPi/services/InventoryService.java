package tn.esprit.projetPi.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.projetPi.dto.InventoryDTO;
import tn.esprit.projetPi.dto.StockMovementDTO;
import tn.esprit.projetPi.entities.*;
import tn.esprit.projetPi.exception.ResourceNotFoundException;
import tn.esprit.projetPi.exception.InsufficientStockException;
import tn.esprit.projetPi.repositories.InventoryRepository;
import tn.esprit.projetPi.repositories.StockMovementRepository;
import tn.esprit.projetPi.repositories.ProductRepository;
import tn.esprit.projetPi.repositories.WarehouseRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    public List<InventoryDTO> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public InventoryDTO getInventoryById(String id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));
        return toDTO(inventory);
    }

    public List<InventoryDTO> getInventoryByProduct(String productId) {
        return inventoryRepository.findByProductId(productId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<InventoryDTO> getInventoryByWarehouse(String warehouseId) {
        return inventoryRepository.findByWarehouseId(warehouseId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<InventoryDTO> getLowStockAlerts() {
        return inventoryRepository.findByIsLowStockTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public InventoryDTO createInventory(InventoryDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + dto.getProductId()));
        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + dto.getWarehouseId()));

        Inventory inventory = new Inventory();
        inventory.setProductId(dto.getProductId());
        inventory.setProductName(product.getName());
        inventory.setSku(product.getSku());
        inventory.setWarehouseId(dto.getWarehouseId());
        inventory.setWarehouseName(warehouse.getName());
        inventory.setLocationCode(dto.getLocationCode());
        inventory.setCurrentStock(dto.getCurrentStock());
        inventory.setReservedStock(dto.getReservedStock() != null ? dto.getReservedStock() : 0);
        inventory.setAvailableStock(dto.getCurrentStock() - (dto.getReservedStock() != null ? dto.getReservedStock() : 0));
        inventory.setLowStockThreshold(dto.getLowStockThreshold() != null ? dto.getLowStockThreshold() : 10);
        inventory.setIsLowStock(inventory.getCurrentStock() <= inventory.getLowStockThreshold());
        inventory.setLastRestockedAt(LocalDateTime.now());

        Inventory saved = inventoryRepository.save(inventory);
        return toDTO(saved);
    }

    public InventoryDTO updateStock(String id, StockMovementDTO movementDTO) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));

        int previousStock = inventory.getCurrentStock();
        int quantity = movementDTO.getQuantity();
        int newStock;

        switch (movementDTO.getType()) {
            case STOCK_IN:
            case RETURN:
                newStock = previousStock + quantity;
                break;
            case STOCK_OUT:
            case DAMAGED:
                if (previousStock < quantity) {
                    throw new InsufficientStockException("Insufficient stock. Available: " + previousStock);
                }
                newStock = previousStock - quantity;
                break;
            case ADJUSTMENT:
                newStock = quantity; // Direct adjustment
                break;
            default:
                throw new IllegalArgumentException("Invalid movement type");
        }

        // Create stock movement record
        StockMovement movement = new StockMovement();
        movement.setInventoryId(id);
        movement.setProductId(inventory.getProductId());
        movement.setWarehouseId(inventory.getWarehouseId());
        movement.setType(movementDTO.getType());
        movement.setQuantity(quantity);
        movement.setPreviousStock(previousStock);
        movement.setNewStock(newStock);
        movement.setReason(movementDTO.getReason());
        movement.setReference(movementDTO.getReference());
        movement.setPerformedBy(movementDTO.getPerformedBy());
        movement.setCreatedAt(LocalDateTime.now());
        stockMovementRepository.save(movement);

        // Update inventory
        inventory.setCurrentStock(newStock);
        inventory.setAvailableStock(newStock - inventory.getReservedStock());
        inventory.setIsLowStock(newStock <= inventory.getLowStockThreshold());
        if (movementDTO.getType() == MovementType.STOCK_IN) {
            inventory.setLastRestockedAt(LocalDateTime.now());
        }

        Inventory saved = inventoryRepository.save(inventory);
        return toDTO(saved);
    }

    public List<StockMovementDTO> getStockMovements(String inventoryId) {
        return stockMovementRepository.findByInventoryId(inventoryId).stream()
                .map(this::toMovementDTO)
                .collect(Collectors.toList());
    }

    public void deleteInventory(String id) {
        if (!inventoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventory not found with id: " + id);
        }
        inventoryRepository.deleteById(id);
    }

    private InventoryDTO toDTO(Inventory inventory) {
        InventoryDTO dto = new InventoryDTO();
        dto.setId(inventory.getId());
        dto.setProductId(inventory.getProductId());
        dto.setProductName(inventory.getProductName());
        dto.setSku(inventory.getSku());
        dto.setWarehouseId(inventory.getWarehouseId());
        dto.setWarehouseName(inventory.getWarehouseName());
        dto.setLocationCode(inventory.getLocationCode());
        dto.setCurrentStock(inventory.getCurrentStock());
        dto.setReservedStock(inventory.getReservedStock());
        dto.setAvailableStock(inventory.getAvailableStock());
        dto.setLowStockThreshold(inventory.getLowStockThreshold());
        dto.setIsLowStock(inventory.getIsLowStock());
        dto.setLastRestockedAt(inventory.getLastRestockedAt());
        return dto;
    }

    private StockMovementDTO toMovementDTO(StockMovement movement) {
        StockMovementDTO dto = new StockMovementDTO();
        dto.setId(movement.getId());
        dto.setInventoryId(movement.getInventoryId());
        dto.setProductId(movement.getProductId());
        dto.setWarehouseId(movement.getWarehouseId());
        dto.setType(movement.getType());
        dto.setQuantity(movement.getQuantity());
        dto.setPreviousStock(movement.getPreviousStock());
        dto.setNewStock(movement.getNewStock());
        dto.setReason(movement.getReason());
        dto.setReference(movement.getReference());
        dto.setPerformedBy(movement.getPerformedBy());
        dto.setCreatedAt(movement.getCreatedAt());
        return dto;
    }
}
