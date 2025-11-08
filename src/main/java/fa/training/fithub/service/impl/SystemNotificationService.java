package fa.training.fithub.service.impl;

import fa.training.fithub.dto.NotificationDTO;
import fa.training.fithub.entity.Notification;
import fa.training.fithub.entity.User;
import fa.training.fithub.enums.NotificationType;
import fa.training.fithub.repository.NotificationRepository;
import fa.training.fithub.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Service xử lý thông báo hệ thống real-time sử dụng Server-Sent Events (SSE)
 */
@Service
@RequiredArgsConstructor
public class SystemNotificationService implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(SystemNotificationService.class);
    private static final Long SSE_TIMEOUT = 30 * 60 * 1000L; // 30 phút

    private final NotificationRepository notificationRepository;

    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> userEmitters = new ConcurrentHashMap<>();

    /**
     * Tạo kết nối SSE cho user
     * 
     * @param userId ID của user
     * @return SseEmitter để gửi events
     */
    public SseEmitter createEmitter(Long userId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        // Lấy hoặc tạo danh sách emitters cho user
        CopyOnWriteArrayList<SseEmitter> emitters = userEmitters.computeIfAbsent(
                userId,
                k -> new CopyOnWriteArrayList<>());

        emitters.add(emitter);
        logger.info("Created SSE emitter for user: {}", userId);

        // Xử lý khi connection complete
        emitter.onCompletion(() -> {
            emitters.remove(emitter);
            logger.info("SSE emitter completed for user: {}", userId);
            if (emitters.isEmpty()) {
                userEmitters.remove(userId);
            }
        });

        // Xử lý khi connection timeout
        emitter.onTimeout(() -> {
            emitters.remove(emitter);
            logger.warn("SSE emitter timeout for user: {}", userId);
            if (emitters.isEmpty()) {
                userEmitters.remove(userId);
            }
        });

        // Xử lý khi có error
        emitter.onError(throwable -> {
            emitters.remove(emitter);
            logger.error("SSE emitter error for user: {}", userId, throwable);
            if (emitters.isEmpty()) {
                userEmitters.remove(userId);
            }
        });

        try {
            // Gửi event đầu tiên để keep-alive connection
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("Connected to notification service"));
        } catch (IOException e) {
            logger.error("Error sending initial SSE event", e);
            emitters.remove(emitter);
        }

        return emitter;
    }

    /**
     * Gửi thông báo đến user thông qua SSE
     * 
     * @param recipient User nhận thông báo
     * @param subject   Tiêu đề (sử dụng như type hoặc reference)
     * @param body      Nội dung thông báo
     */
    @Override
    @Transactional
    public void sendNotification(User recipient, String subject, String body) {
        try {
            // Tạo và lưu notification vào database
            Notification notification = new Notification();
            notification.setUser(recipient);
            notification.setType(NotificationType.SYSTEM_NOTIFICATION);
            notification.setContent(body);
            notification.setReferenceType(subject);
            notification.setIsRead(false);
            notification.setCreatedAt(LocalDateTime.now());

            notification = notificationRepository.save(notification);
            logger.info("Saved notification to database for user: {}", recipient.getId());

            // Chuyển đổi sang DTO
            NotificationDTO notificationDTO = convertToDTO(notification);

            // Gửi qua SSE
            sendToUser(recipient.getId(), notificationDTO);

        } catch (Exception e) {
            logger.error("Error sending notification to user: {}", recipient.getId(), e);
        }
    }

    /**
     * Gửi push notification với custom data
     * 
     * @param recipient  User nhận
     * @param title      Tiêu đề
     * @param body       Nội dung
     * @param customData Dữ liệu tùy chỉnh
     */
    @Override
    @Transactional
    public void sendPushNotification(User recipient, String title, String body, Map<String, String> customData) {
        try {
            Notification notification = new Notification();
            notification.setUser(recipient);
            notification.setContent(body);
            notification.setIsRead(false);
            notification.setCreatedAt(LocalDateTime.now());

            // Xử lý custom data
            if (customData != null) {
                notification.setType(NotificationType.valueOf(
                        customData.getOrDefault("type", "SYSTEM_NOTIFICATION")));
                notification.setReferenceType(customData.get("referenceType"));

                if (customData.containsKey("referenceId")) {
                    try {
                        notification.setReferenceId(Long.parseLong(customData.get("referenceId")));
                    } catch (NumberFormatException e) {
                        logger.warn("Invalid referenceId: {}", customData.get("referenceId"));
                    }
                }

                if (customData.containsKey("senderId")) {
                    try {
                        User sender = new User();
                        sender.setId(Long.parseLong(customData.get("senderId")));
                        notification.setSender(sender);
                    } catch (NumberFormatException e) {
                        logger.warn("Invalid senderId: {}", customData.get("senderId"));
                    }
                }
            } else {
                notification.setType(NotificationType.SYSTEM_NOTIFICATION);
            }

            notification = notificationRepository.save(notification);
            logger.info("Saved push notification for user: {}", recipient.getId());

            NotificationDTO notificationDTO = convertToDTO(notification);
            sendToUser(recipient.getId(), notificationDTO);

        } catch (Exception e) {
            logger.error("Error sending push notification to user: {}", recipient.getId(), e);
        }
    }

    /**
     * Gửi notification DTO đến user qua SSE
     */
    private void sendToUser(Long userId, NotificationDTO notification) {
        CopyOnWriteArrayList<SseEmitter> emitters = userEmitters.get(userId);

        if (emitters == null || emitters.isEmpty()) {
            logger.debug("No active SSE connections for user: {}", userId);
            return;
        }

        // Gửi đến tất cả emitters của user (có thể có nhiều tab/device)
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(notification));
                logger.debug("Sent SSE notification to user: {}", userId);
            } catch (IOException e) {
                logger.error("Error sending SSE to user: {}", userId, e);
                deadEmitters.add(emitter);
            }
        }

        // Xóa các emitters đã chết
        for (SseEmitter deadEmitter : deadEmitters) {
            emitters.remove(deadEmitter);
        }

        if (emitters.isEmpty()) {
            userEmitters.remove(userId);
        }
    }

    /**
     * Lấy danh sách thông báo chưa đọc
     */
    @Override
    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
    }

    /**
     * Đánh dấu một thông báo là đã đọc
     */
    @Override
    @Transactional
    public void markAsRead(String notificationId) {
        try {
            Long id = Long.parseLong(notificationId);
            notificationRepository.findById(id).ifPresent(notification -> {
                notification.setIsRead(true);
                notificationRepository.save(notification);
                logger.info("Marked notification as read: {}", id);
            });
        } catch (NumberFormatException e) {
            logger.error("Invalid notification ID: {}", notificationId, e);
        }
    }

    /**
     * Đánh dấu tất cả thông báo của user là đã đọc
     */
    @Override
    @Transactional
    public void markAllAsRead(User user) {
        notificationRepository.markAllAsReadByUser(user);
        logger.info("Marked all notifications as read for user: {}", user.getId());
    }

    /**
     * Lấy tất cả thông báo của user
     */
    public List<NotificationDTO> getAllNotifications(User user) {
        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lấy số lượng thông báo chưa đọc
     */
    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    /**
     * Chuyển đổi Notification entity sang DTO
     */
    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO.NotificationDTOBuilder builder = NotificationDTO.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .type(notification.getType())
                .referenceId(notification.getReferenceId())
                .referenceType(notification.getReferenceType())
                .content(notification.getContent())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt());

        if (notification.getSender() != null) {
            builder.senderId(notification.getSender().getId())
                    .senderName(notification.getSender().getFullName())
                    .senderAvatar(notification.getSender().getAvatarUrl());
        }

        return builder.build();
    }

    /**
     * Đóng tất cả connections của một user
     */
    public void closeUserConnections(Long userId) {
        CopyOnWriteArrayList<SseEmitter> emitters = userEmitters.remove(userId);
        if (emitters != null) {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.complete();
                } catch (Exception e) {
                    logger.error("Error closing emitter for user: {}", userId, e);
                }
            }
            logger.info("Closed all SSE connections for user: {}", userId);
        }
    }

    /**
     * Lấy số lượng connections hiện tại
     */
    public int getActiveConnectionsCount() {
        return userEmitters.values().stream()
                .mapToInt(List::size)
                .sum();
    }

    /**
     * Kiểm tra user có connection active không
     */
    public boolean hasActiveConnection(Long userId) {
        CopyOnWriteArrayList<SseEmitter> emitters = userEmitters.get(userId);
        return emitters != null && !emitters.isEmpty();
    }
}
