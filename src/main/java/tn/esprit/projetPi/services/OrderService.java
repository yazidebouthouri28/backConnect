package tn.esprit.projetPi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.projetPi.dto.*;
import tn.esprit.projetPi.entities.*;
import tn.esprit.projetPi.exception.InsufficientStockException;
import tn.esprit.projetPi.exception.ResourceNotFoundException;
import tn.esprit.projetPi.repositories.OrderRepository;
import tn.esprit.projetPi.repositories.ProductRepository;
import tn.esprit.projetPi.repositories.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CouponService couponService;
    private final EmailService emailService;
    private final InventoryService inventoryService;

    public PageResponse<OrderDTO> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> orderPage = orderRepository.findAll(pageable);
        return toPageResponse(orderPage);
    }

    public OrderDTO getOrderById(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return toDTO(order);
    }

    public PageResponse<OrderDTO> getOrdersByUser(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> orderPage = orderRepository.findByUserId(userId, pageable);
        return toPageResponse(orderPage);
    }

    public PageResponse<OrderDTO> getOrdersBySeller(String sellerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> orderPage = orderRepository.findBySellerId(sellerId, pageable);
        return toPageResponse(orderPage);
    }

    public PageResponse<OrderDTO> getOrdersByStatus(OrderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> orderPage = orderRepository.findByStatus(status, pageable);
        return toPageResponse(orderPage);
    }

    @Transactional
    public OrderDTO createOrder(String userId, CreateOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Order order = new Order();
        order.setUserId(userId);
        order.setBuyerName(user.getName());
        order.setBuyerEmail(user.getEmail());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setType(request.getType() != null ? request.getType() : OrderType.PURCHASE);
        order.setShippingAddress(toShippingAddress(request.getShippingAddress()));
        order.setBillingAddress(request.getBillingAddress() != null ? 
                toShippingAddress(request.getBillingAddress()) : order.getShippingAddress());
        order.setTrackingNumber(generateTrackingNumber());
        order.setEstimatedDelivery(LocalDateTime.now().plusDays(7));
        order.setPaymentMethod(request.getPaymentMethod());
        order.setCustomerNotes(request.getCustomerNotes());
        order.setCreatedAt(LocalDateTime.now());
        order.setIsPaid(false);

        // Process order items and calculate totals
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        String sellerId = null;

        for (OrderItemDTO itemDTO : request.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemDTO.getProductId()));

            // Check stock availability
            int requestedQuantity = itemDTO.getQuantity();
            int availableStock = product.getAvailableStock() != null ? product.getAvailableStock() : 
                    (product.getStockQuantity() != null ? product.getStockQuantity() : 0);
            
            if (availableStock < requestedQuantity && !Boolean.TRUE.equals(product.getAllowBackorder())) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName() + 
                        ". Available: " + availableStock + ", Requested: " + requestedQuantity);
            }

            // Calculate item price
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductSku(product.getSku());
            orderItem.setSellerId(product.getSellerId());
            orderItem.setSellerName(product.getSellerName());
            orderItem.setQuantity(requestedQuantity);
            orderItem.setImage(product.getMainImage() != null ? product.getMainImage() : 
                    (product.getImages() != null && !product.getImages().isEmpty() ? product.getImages().get(0) : null));
            orderItem.setVariant(itemDTO.getVariant());
            orderItem.setType(request.getType());

            BigDecimal itemTotal;
            if (request.getType() == OrderType.RENTAL && product.getRentalPricePerDay() != null) {
                // Calculate rental price
                int rentalDays = calculateRentalDays(request.getRentalStartDate(), request.getRentalEndDate());
                orderItem.setRentalDays(rentalDays);
                orderItem.setRentalPricePerDay(product.getRentalPricePerDay());
                orderItem.setUnitPrice(product.getRentalPricePerDay());
                itemTotal = product.getRentalPricePerDay()
                        .multiply(BigDecimal.valueOf(rentalDays))
                        .multiply(BigDecimal.valueOf(requestedQuantity));
            } else {
                // Regular purchase price
                BigDecimal unitPrice = product.getDiscountedPrice() != null ? 
                        product.getDiscountedPrice() : product.getPrice();
                orderItem.setUnitPrice(unitPrice);
                itemTotal = unitPrice.multiply(BigDecimal.valueOf(requestedQuantity));
            }

            orderItem.setTotalPrice(itemTotal);
            orderItem.setFinalPrice(itemTotal);
            orderItems.add(orderItem);
            subtotal = subtotal.add(itemTotal);

            // Track seller (for single-seller orders)
            if (sellerId == null) {
                sellerId = product.getSellerId();
            }

            // Reserve stock
            inventoryService.reserveStock(product.getId(), requestedQuantity);
        }

        order.setItems(orderItems);
        order.setSellerId(sellerId);
        order.setSubtotal(subtotal);

        // Apply coupon if provided
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            CouponValidationResponse couponResult = couponService.validateAndApplyCoupon(
                    request.getCouponCode(), userId, subtotal, 
                    orderItems.stream().map(OrderItem::getProductId).collect(Collectors.toList()));
            if (couponResult.getValid()) {
                discountAmount = couponResult.getCalculatedDiscount();
                order.setCouponCode(request.getCouponCode());
            }
        }
        order.setDiscountAmount(discountAmount);

        // Calculate shipping (simplified - can be enhanced)
        BigDecimal shippingCost = calculateShippingCost(orderItems);
        order.setShippingCost(shippingCost);

        // Calculate tax (simplified - can be enhanced)
        BigDecimal taxAmount = subtotal.subtract(discountAmount).multiply(BigDecimal.valueOf(0.1)); // 10% tax
        order.setTaxAmount(taxAmount);

        // Calculate total
        BigDecimal totalAmount = subtotal.subtract(discountAmount).add(shippingCost).add(taxAmount);
        order.setTotalAmount(totalAmount);

        // Set rental details
        if (request.getType() == OrderType.RENTAL) {
            order.setRentalStartDate(request.getRentalStartDate());
            order.setRentalEndDate(request.getRentalEndDate());
            // Calculate deposit (sum of product deposits)
            BigDecimal deposit = orderItems.stream()
                    .map(item -> {
                        Product p = productRepository.findById(item.getProductId()).orElse(null);
                        return p != null && p.getRentalDeposit() != null ? 
                                p.getRentalDeposit().multiply(BigDecimal.valueOf(item.getQuantity())) : BigDecimal.ZERO;
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            order.setRentalDeposit(deposit);
        }

        // Initialize status history
        OrderStatusHistory initialStatus = new OrderStatusHistory();
        initialStatus.setStatus(OrderStatus.PENDING);
        initialStatus.setTimestamp(LocalDateTime.now());
        initialStatus.setNotes("Order created");
        order.setStatusHistory(new ArrayList<>(List.of(initialStatus)));

        Order saved = orderRepository.save(order);

        // Send order confirmation email
        emailService.sendOrderConfirmation(saved);

        log.info("Order created successfully: {}", saved.getId());
        return toDTO(saved);
    }

    @Transactional
    public OrderDTO updateOrderStatus(String id, OrderStatus newStatus, String updatedBy, String notes) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());

        // Add to status history
        OrderStatusHistory history = new OrderStatusHistory();
        history.setStatus(newStatus);
        history.setTimestamp(LocalDateTime.now());
        history.setUpdatedBy(updatedBy);
        history.setNotes(notes);
        if (order.getStatusHistory() == null) {
            order.setStatusHistory(new ArrayList<>());
        }
        order.getStatusHistory().add(history);

        // Handle status-specific logic
        switch (newStatus) {
            case CONFIRMED:
                // Confirm stock reservation
                break;
            case SHIPPED:
                order.setEstimatedDelivery(LocalDateTime.now().plusDays(3));
                emailService.sendOrderShipped(order);
                break;
            case DELIVERED:
                order.setActualDelivery(LocalDateTime.now());
                // Release reserved stock (deduct from inventory)
                for (OrderItem item : order.getItems()) {
                    inventoryService.confirmStockDeduction(item.getProductId(), item.getQuantity());
                }
                emailService.sendOrderDelivered(order);
                break;
            case CANCELLED:
                // Return reserved stock
                for (OrderItem item : order.getItems()) {
                    inventoryService.releaseReservedStock(item.getProductId(), item.getQuantity());
                }
                emailService.sendOrderCancelled(order);
                break;
            default:
                break;
        }

        Order saved = orderRepository.save(order);
        log.info("Order {} status updated from {} to {}", id, oldStatus, newStatus);
        return toDTO(saved);
    }

    public OrderDTO updateTrackingNumber(String id, String trackingNumber, String carrier) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        order.setTrackingNumber(trackingNumber);
        order.setShippingCarrier(carrier);
        order.setUpdatedAt(LocalDateTime.now());
        Order saved = orderRepository.save(order);
        return toDTO(saved);
    }

    @Transactional
    public void cancelOrder(String id, String reason) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel order that has been shipped or delivered. Please request a refund.");
        }

        // Release reserved stock
        for (OrderItem item : order.getItems()) {
            inventoryService.releaseReservedStock(item.getProductId(), item.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());

        // Add to status history
        OrderStatusHistory history = new OrderStatusHistory();
        history.setStatus(OrderStatus.CANCELLED);
        history.setTimestamp(LocalDateTime.now());
        history.setNotes("Order cancelled. Reason: " + reason);
        if (order.getStatusHistory() == null) {
            order.setStatusHistory(new ArrayList<>());
        }
        order.getStatusHistory().add(history);

        orderRepository.save(order);
        emailService.sendOrderCancelled(order);
    }

    public void deleteOrder(String id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }

    public boolean hasUserPurchasedProduct(String userId, String productId) {
        List<Order> orders = orderRepository.findDeliveredOrdersWithProduct(userId, productId);
        return !orders.isEmpty();
    }

    // Helper methods
    private int calculateRentalDays(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 1;
        }
        long days = ChronoUnit.DAYS.between(start, end);
        return days > 0 ? (int) days : 1;
    }

    private BigDecimal calculateShippingCost(List<OrderItem> items) {
        // Simplified shipping calculation - can be enhanced with weight, distance, etc.
        int totalItems = items.stream().mapToInt(OrderItem::getQuantity).sum();
        if (totalItems <= 2) {
            return BigDecimal.valueOf(5.00);
        } else if (totalItems <= 5) {
            return BigDecimal.valueOf(10.00);
        } else {
            return BigDecimal.valueOf(15.00);
        }
    }

    private String generateTrackingNumber() {
        return "TRK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private ShippingAddress toShippingAddress(ShippingAddressDTO dto) {
        if (dto == null) return null;
        ShippingAddress address = new ShippingAddress();
        address.setId(dto.getId() != null ? dto.getId() : UUID.randomUUID().toString());
        address.setFullName(dto.getFullName());
        address.setPhone(dto.getPhone());
        address.setEmail(dto.getEmail());
        address.setAddressLine1(dto.getAddressLine1());
        address.setAddressLine2(dto.getAddressLine2());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPostalCode(dto.getPostalCode());
        address.setCountry(dto.getCountry());
        address.setIsDefault(dto.getIsDefault());
        address.setLabel(dto.getLabel());
        return address;
    }

    private OrderDTO toDTO(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .buyerName(order.getBuyerName())
                .buyerEmail(order.getBuyerEmail())
                .sellerId(order.getSellerId())
                .sellerName(order.getSellerName())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .type(order.getType())
                .items(order.getItems() != null ? order.getItems().stream()
                        .map(this::toOrderItemDTO).collect(Collectors.toList()) : new ArrayList<>())
                .subtotal(order.getSubtotal())
                .discountAmount(order.getDiscountAmount())
                .shippingCost(order.getShippingCost())
                .taxAmount(order.getTaxAmount())
                .totalAmount(order.getTotalAmount())
                .couponCode(order.getCouponCode())
                .shippingAddress(toShippingAddressDTO(order.getShippingAddress()))
                .billingAddress(toShippingAddressDTO(order.getBillingAddress()))
                .trackingNumber(order.getTrackingNumber())
                .shippingCarrier(order.getShippingCarrier())
                .estimatedDelivery(order.getEstimatedDelivery())
                .actualDelivery(order.getActualDelivery())
                .rentalStartDate(order.getRentalStartDate())
                .rentalEndDate(order.getRentalEndDate())
                .rentalDeposit(order.getRentalDeposit())
                .statusHistory(order.getStatusHistory() != null ? order.getStatusHistory().stream()
                        .map(this::toStatusHistoryDTO).collect(Collectors.toList()) : new ArrayList<>())
                .paymentMethod(order.getPaymentMethod())
                .isPaid(order.getIsPaid())
                .paidAt(order.getPaidAt())
                .customerNotes(order.getCustomerNotes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private OrderItemDTO toOrderItemDTO(OrderItem item) {
        return OrderItemDTO.builder()
                .productId(item.getProductId())
                .productName(item.getProductName())
                .productSku(item.getProductSku())
                .sellerId(item.getSellerId())
                .sellerName(item.getSellerName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .type(item.getType())
                .rentalDays(item.getRentalDays())
                .rentalPricePerDay(item.getRentalPricePerDay())
                .image(item.getImage())
                .variant(item.getVariant())
                .discountAmount(item.getDiscountAmount())
                .finalPrice(item.getFinalPrice())
                .build();
    }

    private ShippingAddressDTO toShippingAddressDTO(ShippingAddress address) {
        if (address == null) return null;
        return ShippingAddressDTO.builder()
                .id(address.getId())
                .fullName(address.getFullName())
                .phone(address.getPhone())
                .email(address.getEmail())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .isDefault(address.getIsDefault())
                .label(address.getLabel())
                .build();
    }

    private OrderStatusHistoryDTO toStatusHistoryDTO(OrderStatusHistory history) {
        return OrderStatusHistoryDTO.builder()
                .status(history.getStatus())
                .timestamp(history.getTimestamp())
                .updatedBy(history.getUpdatedBy())
                .notes(history.getNotes())
                .location(history.getLocation())
                .build();
    }

    private PageResponse<OrderDTO> toPageResponse(Page<Order> page) {
        return PageResponse.<OrderDTO>builder()
                .content(page.getContent().stream().map(this::toDTO).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
