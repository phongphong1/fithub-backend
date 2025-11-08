package fa.training.fithub.service;

import fa.training.fithub.entity.Notification;
import fa.training.fithub.entity.User;

import java.util.List;
import java.util.Map;

public interface NotificationService {
    void sendNotification(User recipient, String subject, String body);

    void sendPushNotification(User recipient, String title, String body, Map<String, String> customData);

    List<Notification> getUnreadNotifications(User user);
    void markAsRead(String notificationId);
    void markAllAsRead(User user);
}
