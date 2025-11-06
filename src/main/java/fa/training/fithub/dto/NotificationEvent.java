package fa.training.fithub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event class để gửi qua SSE
 * Đơn giản hóa việc gửi các loại events khác nhau
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    private String eventType;
    private NotificationDTO notification;
    private Long timestamp;

    public static NotificationEvent createNotificationEvent(NotificationDTO notification) {
        return NotificationEvent.builder()
                .eventType("notification")
                .notification(notification)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static NotificationEvent createAlertEvent(NotificationDTO notification) {
        return NotificationEvent.builder()
                .eventType("alert")
                .notification(notification)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static NotificationEvent createUpdateEvent(NotificationDTO notification) {
        return NotificationEvent.builder()
                .eventType("update")
                .notification(notification)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
