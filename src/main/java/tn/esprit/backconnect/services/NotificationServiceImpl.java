package tn.esprit.backconnect.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.backconnect.entities.Notification;
import tn.esprit.backconnect.repositories.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
    }

    @Override
    public Notification createNotification(Notification notification) {
        notification.setSendDate(LocalDateTime.now());
        notification.setIsRead(false);
        return notificationRepository.save(notification);
    }

    @Override
    public Notification updateNotification(Long id, Notification notification) {
        Notification existing = getNotificationById(id);
        existing.setMessage(notification.getMessage());
        return notificationRepository.save(existing);
    }

    @Override
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    @Override
    public List<Notification> getNotificationsByRecipient(Long recipientId) {
        return notificationRepository.findByRecipientUserId(recipientId);
    }

    @Override
    public List<Notification> getUnreadNotifications(Long recipientId) {
        return notificationRepository.findByIsReadFalseAndRecipientUserId(recipientId);
    }

    @Override
    public Notification markAsRead(Long id) {
        Notification notification = getNotificationById(id);
        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }
}
