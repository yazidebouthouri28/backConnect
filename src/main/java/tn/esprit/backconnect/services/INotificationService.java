package tn.esprit.backconnect.services;

import tn.esprit.backconnect.entities.Notification;

import java.util.List;

public interface INotificationService {
    List<Notification> getAllNotifications();

    Notification getNotificationById(Long id);

    Notification createNotification(Notification notification);

    Notification updateNotification(Long id, Notification notification);

    void deleteNotification(Long id);

    List<Notification> getNotificationsByRecipient(Long recipientId);

    List<Notification> getUnreadNotifications(Long recipientId);

    Notification markAsRead(Long id);
}
