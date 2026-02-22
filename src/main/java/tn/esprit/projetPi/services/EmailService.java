package tn.esprit.projetPi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.projetPi.entities.*;
import tn.esprit.projetPi.repositories.EmailNotificationRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock Email Service - Logs emails to console instead of sending real emails.
 * In production, this would be replaced with actual SMTP/SendGrid/etc. integration.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final EmailNotificationRepository emailNotificationRepository;

    public void sendWelcomeEmail(User user) {
        Map<String, String> data = new HashMap<>();
        data.put("name", user.getName());
        data.put("username", user.getUsername());

        String subject = "Welcome to ConnectCamp!";
        String body = String.format(
            "Hello %s,\n\n" +
            "Welcome to ConnectCamp! Your account has been created successfully.\n\n" +
            "Username: %s\n\n" +
            "Start exploring our products and enjoy shopping!\n\n" +
            "Best regards,\nConnectCamp Team",
            user.getName(), user.getUsername()
        );

        sendEmail(user.getId(), user.getEmail(), user.getName(), EmailType.WELCOME, subject, body, data);
    }

    public void sendEmailVerification(User user, String verificationToken) {
        Map<String, String> data = new HashMap<>();
        data.put("name", user.getName());
        data.put("token", verificationToken);

        String subject = "Verify Your Email - ConnectCamp";
        String body = String.format(
            "Hello %s,\n\n" +
            "Please verify your email address by using the following token:\n\n" +
            "Verification Token: %s\n\n" +
            "If you didn't create an account, please ignore this email.\n\n" +
            "Best regards,\nConnectCamp Team",
            user.getName(), verificationToken
        );

        sendEmail(user.getId(), user.getEmail(), user.getName(), EmailType.EMAIL_VERIFICATION, subject, body, data);
    }

    public void sendPasswordReset(User user, String resetToken) {
        Map<String, String> data = new HashMap<>();
        data.put("name", user.getName());
        data.put("token", resetToken);

        String subject = "Password Reset - ConnectCamp";
        String body = String.format(
            "Hello %s,\n\n" +
            "You requested to reset your password. Use the following token:\n\n" +
            "Reset Token: %s\n\n" +
            "This token will expire in 1 hour.\n\n" +
            "If you didn't request this, please ignore this email.\n\n" +
            "Best regards,\nConnectCamp Team",
            user.getName(), resetToken
        );

        sendEmail(user.getId(), user.getEmail(), user.getName(), EmailType.PASSWORD_RESET, subject, body, data);
    }

    public void sendOrderConfirmation(Order order) {
        Map<String, String> data = new HashMap<>();
        data.put("orderId", order.getId());
        data.put("buyerName", order.getBuyerName());
        data.put("total", order.getTotalAmount().toString());

        String subject = "Order Confirmation #" + order.getId() + " - ConnectCamp";
        String body = String.format(
            "Hello %s,\n\n" +
            "Thank you for your order!\n\n" +
            "Order ID: %s\n" +
            "Order Date: %s\n" +
            "Total Amount: $%s\n" +
            "Status: %s\n\n" +
            "You can track your order status in your account dashboard.\n\n" +
            "Best regards,\nConnectCamp Team",
            order.getBuyerName(),
            order.getId(),
            order.getOrderDate(),
            order.getTotalAmount(),
            order.getStatus()
        );

        sendEmail(order.getUserId(), order.getBuyerEmail(), order.getBuyerName(), 
                EmailType.ORDER_CONFIRMATION, subject, body, data);
    }

    public void sendOrderShipped(Order order) {
        Map<String, String> data = new HashMap<>();
        data.put("orderId", order.getId());
        data.put("trackingNumber", order.getTrackingNumber());
        data.put("carrier", order.getShippingCarrier());

        String subject = "Your Order Has Shipped! #" + order.getId() + " - ConnectCamp";
        String body = String.format(
            "Hello %s,\n\n" +
            "Great news! Your order has been shipped!\n\n" +
            "Order ID: %s\n" +
            "Tracking Number: %s\n" +
            "Carrier: %s\n" +
            "Estimated Delivery: %s\n\n" +
            "Best regards,\nConnectCamp Team",
            order.getBuyerName(),
            order.getId(),
            order.getTrackingNumber(),
            order.getShippingCarrier() != null ? order.getShippingCarrier() : "Standard Shipping",
            order.getEstimatedDelivery()
        );

        sendEmail(order.getUserId(), order.getBuyerEmail(), order.getBuyerName(),
                EmailType.ORDER_SHIPPED, subject, body, data);
    }

    public void sendOrderDelivered(Order order) {
        Map<String, String> data = new HashMap<>();
        data.put("orderId", order.getId());

        String subject = "Your Order Has Been Delivered! #" + order.getId() + " - ConnectCamp";
        String body = String.format(
            "Hello %s,\n\n" +
            "Your order has been delivered!\n\n" +
            "Order ID: %s\n" +
            "Delivered: %s\n\n" +
            "We hope you enjoy your purchase! Please consider leaving a review.\n\n" +
            "Best regards,\nConnectCamp Team",
            order.getBuyerName(),
            order.getId(),
            order.getActualDelivery()
        );

        sendEmail(order.getUserId(), order.getBuyerEmail(), order.getBuyerName(),
                EmailType.ORDER_DELIVERED, subject, body, data);
    }

    public void sendOrderCancelled(Order order) {
        Map<String, String> data = new HashMap<>();
        data.put("orderId", order.getId());

        String subject = "Order Cancelled #" + order.getId() + " - ConnectCamp";
        String body = String.format(
            "Hello %s,\n\n" +
            "Your order has been cancelled.\n\n" +
            "Order ID: %s\n" +
            "Total Amount: $%s\n\n" +
            "If you paid for this order, a refund will be processed within 5-7 business days.\n\n" +
            "Best regards,\nConnectCamp Team",
            order.getBuyerName(),
            order.getId(),
            order.getTotalAmount()
        );

        sendEmail(order.getUserId(), order.getBuyerEmail(), order.getBuyerName(),
                EmailType.ORDER_CANCELLED, subject, body, data);
    }

    public void sendRefundApproved(RefundRequest refund, String userEmail, String userName) {
        Map<String, String> data = new HashMap<>();
        data.put("refundId", refund.getId());
        data.put("orderId", refund.getOrderId());

        String subject = "Refund Approved - ConnectCamp";
        String body = String.format(
            "Hello %s,\n\n" +
            "Your refund request has been approved!\n\n" +
            "Refund ID: %s\n" +
            "Order ID: %s\n" +
            "Approved Amount: $%s\n\n" +
            "The refund will be processed within 5-7 business days.\n\n" +
            "Best regards,\nConnectCamp Team",
            userName,
            refund.getId(),
            refund.getOrderId(),
            refund.getApprovedAmount()
        );

        sendEmail(refund.getUserId(), userEmail, userName,
                EmailType.REFUND_APPROVED, subject, body, data);
    }

    public void sendRefundCompleted(RefundRequest refund, String userEmail, String userName) {
        Map<String, String> data = new HashMap<>();
        data.put("refundId", refund.getId());
        data.put("orderId", refund.getOrderId());

        String subject = "Refund Completed - ConnectCamp";
        String body = String.format(
            "Hello %s,\n\n" +
            "Your refund has been completed!\n\n" +
            "Refund ID: %s\n" +
            "Order ID: %s\n" +
            "Refunded Amount: $%s\n\n" +
            "The funds should appear in your account within 3-5 business days.\n\n" +
            "Best regards,\nConnectCamp Team",
            userName,
            refund.getId(),
            refund.getOrderId(),
            refund.getApprovedAmount()
        );

        sendEmail(refund.getUserId(), userEmail, userName,
                EmailType.REFUND_COMPLETED, subject, body, data);
    }

    public void sendLowStockAlert(Product product, String sellerEmail, String sellerName) {
        Map<String, String> data = new HashMap<>();
        data.put("productId", product.getId());
        data.put("productName", product.getName());
        data.put("currentStock", String.valueOf(product.getAvailableStock()));

        String subject = "Low Stock Alert: " + product.getName() + " - ConnectCamp";
        String body = String.format(
            "Hello %s,\n\n" +
            "Your product is running low on stock!\n\n" +
            "Product: %s\n" +
            "Current Stock: %d\n" +
            "Threshold: %d\n\n" +
            "Please restock soon to avoid missing sales.\n\n" +
            "Best regards,\nConnectCamp Team",
            sellerName,
            product.getName(),
            product.getAvailableStock(),
            product.getLowStockThreshold()
        );

        sendEmail(product.getSellerId(), sellerEmail, sellerName,
                EmailType.STOCK_ALERT, subject, body, data);
    }

    public void sendReviewReminder(Order order) {
        Map<String, String> data = new HashMap<>();
        data.put("orderId", order.getId());

        String subject = "Share Your Experience - Review Your Purchase!";
        String body = String.format(
            "Hello %s,\n\n" +
            "We hope you're enjoying your recent purchase!\n\n" +
            "Order ID: %s\n\n" +
            "Your feedback helps other shoppers make informed decisions.\n" +
            "Please take a moment to review your items.\n\n" +
            "Best regards,\nConnectCamp Team",
            order.getBuyerName(),
            order.getId()
        );

        sendEmail(order.getUserId(), order.getBuyerEmail(), order.getBuyerName(),
                EmailType.REVIEW_REMINDER, subject, body, data);
    }

    private void sendEmail(String userId, String toEmail, String toName, EmailType type,
                           String subject, String body, Map<String, String> data) {
        // Create email notification record
        EmailNotification notification = new EmailNotification();
        notification.setUserId(userId);
        notification.setToEmail(toEmail);
        notification.setToName(toName);
        notification.setType(type);
        notification.setSubject(subject);
        notification.setBody(body);
        notification.setTemplateData(data);
        notification.setCreatedAt(LocalDateTime.now());

        // Mock sending - just log to console
        log.info("\n" +
                "========================================\n" +
                "MOCK EMAIL SENT\n" +
                "========================================\n" +
                "To: {} <{}>\n" +
                "Subject: {}\n" +
                "Type: {}\n" +
                "----------------------------------------\n" +
                "{}\n" +
                "========================================\n",
                toName, toEmail, subject, type, body);

        // Mark as sent in mock mode
        notification.setSent(true);
        notification.setSentAt(LocalDateTime.now());
        notification.setRetryCount(0);

        emailNotificationRepository.save(notification);
    }
}
