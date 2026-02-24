package tn.esprit.backconnect.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.backconnect.entities.Notification;
import tn.esprit.backconnect.services.INotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification management APIs")
public class NotificationController {

    private final INotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get all notifications")
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get notification by ID")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new notification")
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        return new ResponseEntity<>(notificationService.createNotification(notification), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a notification")
    public ResponseEntity<Notification> updateNotification(@PathVariable Long id,
            @RequestBody Notification notification) {
        return ResponseEntity.ok(notificationService.updateNotification(id, notification));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a notification")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recipient/{recipientId}")
    @Operation(summary = "Get notifications by recipient")
    public ResponseEntity<List<Notification>> getNotificationsByRecipient(@PathVariable Long recipientId) {
        return ResponseEntity.ok(notificationService.getNotificationsByRecipient(recipientId));
    }

    @GetMapping("/recipient/{recipientId}/unread")
    @Operation(summary = "Get unread notifications by recipient")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long recipientId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(recipientId));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark notification as read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }
}
