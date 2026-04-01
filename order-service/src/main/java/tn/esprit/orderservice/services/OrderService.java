package tn.esprit.orderservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.orderservice.entities.*;
import tn.esprit.orderservice.enums.OrderStatus;
import tn.esprit.orderservice.enums.PaymentStatus;
import tn.esprit.orderservice.events.OrderEvent;
import tn.esprit.orderservice.events.OrderEventPublisher;
import tn.esprit.orderservice.exception.BusinessException;
import tn.esprit.orderservice.exception.ResourceNotFoundException;
import tn.esprit.orderservice.repositories.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final OrderEventPublisher eventPublisher;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Page<Order> getOrdersByUser(UUID userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }

    public Page<Order> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }

    public Order getOrderById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    public Order getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with number: " + orderNumber));
    }

    // ✅ Méthode publique SANS @Transactional — Kafka publié APRÈS le commit
    public Order createOrderFromCart(UUID userId, String userEmail,
                                     String shippingName, String shippingPhone,
                                     String shippingAddress, String shippingCity,
                                     String shippingPostalCode, String shippingCountry,
                                     String paymentMethod, String notes) {

        // 1. Sauvegarder l'order dans une transaction séparée (commit immédiat)
        Order savedOrder = createOrderInTransaction(
                userId, userEmail,
                shippingName, shippingPhone,
                shippingAddress, shippingCity,
                shippingPostalCode, shippingCountry,
                paymentMethod, notes
        );

        // 2. Publier l'event Kafka APRÈS le commit JPA
        try {
            OrderEvent event = buildOrderEvent(savedOrder);
            eventPublisher.publishOrderCreated(event);
            log.info("Order created and event published: {} ({})", savedOrder.getOrderNumber(), savedOrder.getId());
        } catch (Exception e) {
            // Ne pas faire échouer la commande si Kafka est down
            log.error("Failed to publish Kafka event for order {}, but order was saved successfully: {}",
                    savedOrder.getOrderNumber(), e.getMessage());
        }

        return savedOrder;
    }

    // ✅ Transaction isolée — commit avant retour
    @Transactional
    public Order createOrderInTransaction(UUID userId, String userEmail,
                                          String shippingName, String shippingPhone,
                                          String shippingAddress, String shippingCity,
                                          String shippingPostalCode, String shippingCountry,
                                          String paymentMethod, String notes) {

        log.info("Creating order for userId={}", userId);

        Cart cart = cartService.getCartByUserId(userId);
        log.info("Cart found: {} items", cart.getItems() != null ? cart.getItems().size() : 0);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BusinessException("Cart is empty. Cannot create order.");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            BigDecimal itemPrice = cartItem.getPrice() != null ? cartItem.getPrice() : BigDecimal.ZERO;
            BigDecimal lineTotal = itemPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .productId(cartItem.getProductId())
                    .productName(cartItem.getProductName())
                    .productSku(null)
                    .productThumbnail(cartItem.getProductThumbnail())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(itemPrice)
                    .totalPrice(lineTotal)
                    .selectedVariant(cartItem.getSelectedVariant())
                    .selectedColor(cartItem.getSelectedColor())
                    .selectedSize(cartItem.getSelectedSize())
                    .build();

            orderItems.add(orderItem);
            subtotal = subtotal.add(lineTotal);
        }

        BigDecimal shippingCost = calculateShippingCost(subtotal);
        BigDecimal taxAmount = subtotal.multiply(BigDecimal.valueOf(0.19));
        BigDecimal discountAmount = cart.getDiscountAmount() != null
                ? cart.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal totalAmount = subtotal.add(shippingCost).add(taxAmount).subtract(discountAmount);

        Order order = Order.builder()
                .userId(userId)
                .userEmail(userEmail)
                .subtotal(subtotal)
                .shippingCost(shippingCost)
                .taxAmount(taxAmount)
                .discountAmount(discountAmount)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod(paymentMethod)
                .shippingName(shippingName)
                .shippingPhone(shippingPhone)
                .shippingAddress(shippingAddress)
                .shippingCity(shippingCity)
                .shippingPostalCode(shippingPostalCode)
                .shippingCountry(shippingCountry)
                .couponCode(cart.getAppliedCouponCode())
                .notes(notes)
                .build();

        // Lier les items à l'order AVANT le save
        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }
        order.setItems(orderItems);

        Order saved = orderRepository.save(order);
        log.info("Order saved successfully: {} ({})", saved.getOrderNumber(), saved.getId());

        // Clear cart dans la même transaction
        cartService.clearCart(userId);
        log.info("Cart cleared for userId={}", userId);

        return saved;
    }

    @Transactional
    public Order updateOrderStatus(UUID orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);

        switch (status) {
            case SHIPPED:
                order.setShippedAt(LocalDateTime.now());
                break;
            case DELIVERED:
                order.setDeliveredAt(LocalDateTime.now());
                Order deliveredOrder = orderRepository.save(order);
                try {
                    eventPublisher.publishOrderCompleted(buildOrderEvent(deliveredOrder));
                    log.info("Order completed event published: {}", deliveredOrder.getOrderNumber());
                } catch (Exception e) {
                    log.error("Failed to publish completed event: {}", e.getMessage());
                }
                return deliveredOrder;
            case CANCELLED:
                order.setCancelledAt(LocalDateTime.now());
                Order cancelledOrder = orderRepository.save(order);
                try {
                    eventPublisher.publishOrderCancelled(buildOrderEvent(cancelledOrder));
                    log.info("Order cancelled event published: {}", cancelledOrder.getOrderNumber());
                } catch (Exception e) {
                    log.error("Failed to publish cancelled event: {}", e.getMessage());
                }
                return cancelledOrder;
            default:
                break;
        }

        return orderRepository.save(order);
    }

    @Transactional
    public Order updatePaymentStatus(UUID orderId, PaymentStatus status, String transactionId) {
        Order order = getOrderById(orderId);
        order.setPaymentStatus(status);
        order.setPaymentTransactionId(transactionId);
        if (status == PaymentStatus.COMPLETED) {
            order.setPaidAt(LocalDateTime.now());
            order.setStatus(OrderStatus.CONFIRMED);
        }
        return orderRepository.save(order);
    }

    public BigDecimal getTotalRevenue() {
        BigDecimal revenue = orderRepository.getTotalRevenue();
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    private BigDecimal calculateShippingCost(BigDecimal subtotal) {
        if (subtotal.compareTo(BigDecimal.valueOf(100)) >= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(7);
    }

    private OrderEvent buildOrderEvent(Order order) {
        List<OrderEvent.OrderItemEvent> itemEvents = order.getItems() != null
                ? order.getItems().stream()
                .map(item -> OrderEvent.OrderItemEvent.builder()
                        .productId(item.getProductId() != null ? item.getProductId().toString() : null)
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .build())
                .collect(Collectors.toList())
                : new ArrayList<>();

        return OrderEvent.builder()
                .orderId(order.getId().toString())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId() != null ? order.getUserId().toString() : null)
                .userEmail(order.getUserEmail())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .paymentStatus(order.getPaymentStatus() != null ? order.getPaymentStatus().name() : null)
                .items(itemEvents)
                .timestamp(LocalDateTime.now())
                .build();
    }
}