package tn.esprit.projetPi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.projetPi.dto.*;
import tn.esprit.projetPi.entities.*;
import tn.esprit.projetPi.exception.InsufficientStockException;
import tn.esprit.projetPi.exception.ResourceNotFoundException;
import tn.esprit.projetPi.repositories.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final ProductRepository productRepository;
    private final StockAlertRepository stockAlertRepository;
    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private final UserRepository userRepository;

    /**
     * Reserve stock for an order (doesn't deduct yet, just marks as reserved)
     */
    @Transactional
    public void reserveStock(String productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        int currentStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
        int reserved = product.getReservedQuantity() != null ? product.getReservedQuantity() : 0;
        int available = currentStock - reserved;

        if (available < quantity && !Boolean.TRUE.equals(product.getAllowBackorder())) {
            throw new InsufficientStockException(
                    "Insufficient stock for product: " + product.getName() + 
                    ". Available: " + available + ", Requested: " + quantity);
        }

        product.setReservedQuantity(reserved + quantity);
        updateAvailableStock(product);
        productRepository.save(product);

        log.info("Reserved {} units of product {}", quantity, productId);
    }

    /**
     * Release reserved stock (e.g., when order is cancelled)
     */
    @Transactional
    public void releaseReservedStock(String productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        int reserved = product.getReservedQuantity() != null ? product.getReservedQuantity() : 0;
        product.setReservedQuantity(Math.max(0, reserved - quantity));
        updateAvailableStock(product);
        productRepository.save(product);

        log.info("Released {} reserved units of product {}", quantity, productId);
    }

    /**
     * Confirm stock deduction (when order is delivered)
     */
    @Transactional
    public void confirmStockDeduction(String productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        int currentStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
        int reserved = product.getReservedQuantity() != null ? product.getReservedQuantity() : 0;

        // Deduct from actual stock
        product.setStockQuantity(Math.max(0, currentStock - quantity));
        // Release from reserved
        product.setReservedQuantity(Math.max(0, reserved - quantity));
        // Update available
        updateAvailableStock(product);
        // Update purchase count
        product.setPurchaseCount((product.getPurchaseCount() != null ? product.getPurchaseCount() : 0) + quantity);
        
        productRepository.save(product);

        // Check for low stock alert
        checkLowStockAlert(product);

        // Record stock movement
        recordStockMovement(productId, MovementType.SALE, -quantity, "Order completed");

        log.info("Confirmed deduction of {} units from product {}", quantity, productId);
    }

    /**
     * Add stock to a product
     */
    @Transactional
    public void addStock(String productId, int quantity, String reason) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        int currentStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
        product.setStockQuantity(currentStock + quantity);
        updateAvailableStock(product);
        productRepository.save(product);

        // Record stock movement
        recordStockMovement(productId, MovementType.PURCHASE, quantity, reason);

        // Clear out-of-stock alert if applicable
        if (product.getAvailableStock() > 0) {
            createBackInStockAlert(product);
        }

        log.info("Added {} units to product {}", quantity, productId);
    }

    /**
     * Remove stock from a product
     */
    @Transactional
    public void removeStock(String productId, int quantity, String reason) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        int currentStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
        int newStock = currentStock - quantity;
        
        // Prevent negative stock
        if (newStock < 0) {
            throw new InsufficientStockException(
                    "Cannot remove " + quantity + " units. Current stock: " + currentStock);
        }

        product.setStockQuantity(newStock);
        updateAvailableStock(product);
        productRepository.save(product);

        // Record stock movement
        recordStockMovement(productId, MovementType.ADJUSTMENT, -quantity, reason);

        // Check for alerts
        checkLowStockAlert(product);

        log.info("Removed {} units from product {}", quantity, productId);
    }

    /**
     * Set absolute stock quantity
     */
    @Transactional
    public void setStock(String productId, int quantity, String reason) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        int previousStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
        int difference = quantity - previousStock;

        product.setStockQuantity(quantity);
        
        // If reserved exceeds new stock, cap it
        if (product.getReservedQuantity() != null && product.getReservedQuantity() > quantity) {
            product.setReservedQuantity(quantity);
        }
        
        updateAvailableStock(product);
        productRepository.save(product);

        // Record stock movement
        recordStockMovement(productId, MovementType.ADJUSTMENT, difference, reason);

        // Check for alerts
        checkLowStockAlert(product);

        log.info("Set stock of product {} to {}", productId, quantity);
    }

    /**
     * Get low stock products for a seller
     */
    public List<ProductDTO> getLowStockProducts(String sellerId) {
        List<Product> products;
        if (sellerId != null) {
            products = productRepository.findOutOfStockBySelerId(sellerId);
        } else {
            products = productRepository.findOutOfStockProducts();
        }
        return products.stream().map(this::toProductDTO).collect(Collectors.toList());
    }

    /**
     * Get stock alerts for a seller
     */
    public List<StockAlert> getStockAlerts(String sellerId, boolean unacknowledgedOnly) {
        if (unacknowledgedOnly) {
            return stockAlertRepository.findBySellerIdAndAcknowledged(sellerId, false);
        }
        return stockAlertRepository.findBySellerId(sellerId);
    }

    /**
     * Acknowledge a stock alert
     */
    public void acknowledgeAlert(String alertId, String userId) {
        StockAlert alert = stockAlertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found: " + alertId));
        alert.setAcknowledged(true);
        alert.setAcknowledgedBy(userId);
        alert.setAcknowledgedAt(LocalDateTime.now());
        stockAlertRepository.save(alert);
    }

    // Helper methods
    private void updateAvailableStock(Product product) {
        int stock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
        int reserved = product.getReservedQuantity() != null ? product.getReservedQuantity() : 0;
        int available = Math.max(0, stock - reserved);
        
        product.setAvailableStock(available);
        product.setInStock(available > 0);
    }

    private void checkLowStockAlert(Product product) {
        int threshold = product.getLowStockThreshold() != null ? product.getLowStockThreshold() : 10;
        int available = product.getAvailableStock() != null ? product.getAvailableStock() : 0;

        if (available == 0) {
            createOutOfStockAlert(product);
        } else if (available <= threshold) {
            createLowStockAlert(product);
        }
    }

    private void createLowStockAlert(Product product) {
        StockAlert alert = new StockAlert();
        alert.setProductId(product.getId());
        alert.setProductName(product.getName());
        alert.setSellerId(product.getSellerId());
        alert.setType(AlertType.LOW_STOCK);
        alert.setCurrentStock(product.getAvailableStock());
        alert.setThreshold(product.getLowStockThreshold());
        alert.setAcknowledged(false);
        alert.setCreatedAt(LocalDateTime.now());
        stockAlertRepository.save(alert);

        log.warn("Low stock alert created for product {}: {} units remaining", 
                product.getId(), product.getAvailableStock());
    }

    private void createOutOfStockAlert(Product product) {
        StockAlert alert = new StockAlert();
        alert.setProductId(product.getId());
        alert.setProductName(product.getName());
        alert.setSellerId(product.getSellerId());
        alert.setType(AlertType.OUT_OF_STOCK);
        alert.setCurrentStock(0);
        alert.setThreshold(product.getLowStockThreshold());
        alert.setAcknowledged(false);
        alert.setCreatedAt(LocalDateTime.now());
        stockAlertRepository.save(alert);

        log.warn("Out of stock alert created for product {}", product.getId());
    }

    private void createBackInStockAlert(Product product) {
        StockAlert alert = new StockAlert();
        alert.setProductId(product.getId());
        alert.setProductName(product.getName());
        alert.setSellerId(product.getSellerId());
        alert.setType(AlertType.BACK_IN_STOCK);
        alert.setCurrentStock(product.getAvailableStock());
        alert.setThreshold(product.getLowStockThreshold());
        alert.setAcknowledged(false);
        alert.setCreatedAt(LocalDateTime.now());
        stockAlertRepository.save(alert);

        log.info("Back in stock alert created for product {}", product.getId());
    }

    private void recordStockMovement(String productId, MovementType type, int quantity, String reason) {
        StockMovement movement = new StockMovement();
        movement.setProductId(productId);
        movement.setType(type);
        movement.setQuantity(quantity);
        movement.setReason(reason);
        movement.setCreatedAt(LocalDateTime.now());
        stockMovementRepository.save(movement);
    }

    private ProductDTO toProductDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .sellerId(product.getSellerId())
                .sellerName(product.getSellerName())
                .stockQuantity(product.getStockQuantity())
                .availableStock(product.getAvailableStock())
                .lowStockThreshold(product.getLowStockThreshold())
                .inStock(product.getInStock())
                .price(product.getPrice())
                .mainImage(product.getMainImage())
                .build();
    }

    // ===== Inventory Entity Methods =====

    public List<InventoryDTO> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(this::toInventoryDTO)
                .collect(Collectors.toList());
    }

    public InventoryDTO getInventoryById(String id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found: " + id));
        return toInventoryDTO(inventory);
    }

    public List<InventoryDTO> getInventoryByProduct(String productId) {
        return inventoryRepository.findByProductId(productId).stream()
                .map(this::toInventoryDTO)
                .collect(Collectors.toList());
    }

    public List<InventoryDTO> getInventoryByWarehouse(String warehouseId) {
        return inventoryRepository.findByWarehouseId(warehouseId).stream()
                .map(this::toInventoryDTO)
                .collect(Collectors.toList());
    }

    public List<InventoryDTO> getLowStockAlerts() {
        return inventoryRepository.findByIsLowStockTrue().stream()
                .map(this::toInventoryDTO)
                .collect(Collectors.toList());
    }

    public InventoryDTO createInventory(InventoryDTO dto) {
        Inventory inventory = new Inventory();
        inventory.setProductId(dto.getProductId());
        inventory.setProductName(dto.getProductName());
        inventory.setSku(dto.getSku());
        inventory.setWarehouseId(dto.getWarehouseId());
        inventory.setWarehouseName(dto.getWarehouseName());
        inventory.setLocationCode(dto.getLocationCode());
        inventory.setCurrentStock(dto.getCurrentStock() != null ? dto.getCurrentStock() : 0);
        inventory.setReservedStock(dto.getReservedStock() != null ? dto.getReservedStock() : 0);
        inventory.setAvailableStock(inventory.getCurrentStock() - inventory.getReservedStock());
        inventory.setLowStockThreshold(dto.getLowStockThreshold() != null ? dto.getLowStockThreshold() : 10);
        inventory.setIsLowStock(inventory.getAvailableStock() <= inventory.getLowStockThreshold());
        inventory.setLastRestockedAt(LocalDateTime.now());
        
        Inventory saved = inventoryRepository.save(inventory);
        log.info("Inventory created for product {} in warehouse {}", dto.getProductId(), dto.getWarehouseId());
        return toInventoryDTO(saved);
    }

    @Transactional
    public InventoryDTO updateStock(String inventoryId, StockMovementDTO movementDTO) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found: " + inventoryId));

        int previousStock = inventory.getCurrentStock() != null ? inventory.getCurrentStock() : 0;
        int newStock = previousStock;

        switch (movementDTO.getType()) {
            case STOCK_IN:
            case PURCHASE:
            case RETURN:
                newStock = previousStock + movementDTO.getQuantity();
                inventory.setLastRestockedAt(LocalDateTime.now());
                break;
            case STOCK_OUT:
            case SALE:
            case DAMAGED:
                newStock = Math.max(0, previousStock - movementDTO.getQuantity());
                break;
            case ADJUSTMENT:
            case TRANSFER:
                newStock = previousStock + movementDTO.getQuantity();
                break;
        }

        inventory.setCurrentStock(newStock);
        int reserved = inventory.getReservedStock() != null ? inventory.getReservedStock() : 0;
        inventory.setAvailableStock(Math.max(0, newStock - reserved));
        inventory.setIsLowStock(inventory.getAvailableStock() <= 
                (inventory.getLowStockThreshold() != null ? inventory.getLowStockThreshold() : 10));

        // Record the movement
        StockMovement movement = new StockMovement();
        movement.setInventoryId(inventoryId);
        movement.setProductId(inventory.getProductId());
        movement.setWarehouseId(inventory.getWarehouseId());
        movement.setType(movementDTO.getType());
        movement.setQuantity(movementDTO.getQuantity());
        movement.setPreviousStock(previousStock);
        movement.setNewStock(newStock);
        movement.setReason(movementDTO.getReason());
        movement.setReference(movementDTO.getReference());
        movement.setPerformedBy(movementDTO.getPerformedBy());
        movement.setCreatedAt(LocalDateTime.now());
        stockMovementRepository.save(movement);

        Inventory saved = inventoryRepository.save(inventory);
        log.info("Inventory {} stock updated: {} -> {}", inventoryId, previousStock, newStock);
        return toInventoryDTO(saved);
    }

    public List<StockMovementDTO> getStockMovements(String inventoryId) {
        return stockMovementRepository.findByInventoryId(inventoryId).stream()
                .map(this::toStockMovementDTO)
                .collect(Collectors.toList());
    }

    public void deleteInventory(String id) {
        if (!inventoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventory not found: " + id);
        }
        inventoryRepository.deleteById(id);
        log.info("Inventory {} deleted", id);
    }

    private InventoryDTO toInventoryDTO(Inventory inventory) {
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

    private StockMovementDTO toStockMovementDTO(StockMovement movement) {
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
