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

/**
 * Order Service - migrated from monolith with added Kafka event publishing.
 *
 * Kafka Events Published:
 * - order.created: When a new order is placed from cart
 * - order.completed: When order status becomes DELIVERED
 * - order.cancelled: When order is cancelled (triggers stock restoration in Product Service)
 *
 * Key Changes from Monolith:
 * - No direct UserService/ProductRepository dependency
 * - Uses CartService for cart operations
 * - Product data is read from denormalized CartItem fields
 * - User ID comes from API Gateway headers
 */
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

    /**
     * Create an order from the user's cart.
     * Product data is taken from the denormalized CartItem fields.
     * After creation, a Kafka event is published so Product Service can decrement stock.
     */
    @Transactional
    public Order createOrderFromCart(UUID userId, String userEmail,
                                     String shippingName, String shippingPhone,
                                     String shippingAddress, String shippingCity,
                                     String shippingPostalCode, String shippingCountry,
                                     String paymentMethod, String notes) {
        Cart cart = cartService.getCartByUserId(userId);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BusinessException("Cart is empty. Cannot create order.");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            BigDecimal itemPrice = cartItem.getPrice() != null ? cartItem.getPrice() : BigDecimal.ZERO;
            BigDecimal lineTotal = itemPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            // Create order item with denormalized product snapshot data
            OrderItem orderItem = OrderItem.builder()
                    .productId(cartItem.getProductId())
                    .productName(cartItem.getProductName())
                    .productSku(null) // Will be filled if available
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

        order = orderRepository.save(order);

        // Link items to order
        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }
        order.setItems(orderItems);
        order = orderRepository.save(order);

        // Clear the cart after successful order creation
        cartService.clearCart(userId);

        // Publish Kafka event - Product Service will decrement stock
        OrderEvent event = buildOrderEvent(order);
        eventPublisher.publishOrderCreated(event);
        log.info("Order created and event published: {} ({})", order.getOrderNumber(), order.getId());

        return order;
    }

    /**
     * Update order status. Publishes events for completed/cancelled orders.
     */
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
                // Publish order.completed event
                OrderEvent completedEvent = buildOrderEvent(order);
                eventPublisher.publishOrderCompleted(completedEvent);
                log.info("Order completed event published: {}", order.getOrderNumber());
                break;
            case CANCELLED:
                order.setCancelledAt(LocalDateTime.now());
                // Publish order.cancelled event - Product Service will restore stock
                OrderEvent cancelledEvent = buildOrderEvent(order);
                eventPublisher.publishOrderCancelled(cancelledEvent);
                log.info("Order cancelled event published: {}", order.getOrderNumber());
                break;
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
            return BigDecimal.ZERO; // Free shipping for orders over 100
        }
        return BigDecimal.valueOf(7); // Fixed shipping cost
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
