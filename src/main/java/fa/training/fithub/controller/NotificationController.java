package fa.training.fithub.controller;

import fa.training.fithub.dto.NotificationDTO;
import fa.training.fithub.entity.User;
import fa.training.fithub.service.impl.SystemNotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller xử lý các API liên quan đến thông báo
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final SystemNotificationService notificationService;

    /**
     * Endpoint SSE để nhận thông báo real-time
     * 
     * GET /api/notifications/stream
     * 
     * @param authentication Authentication object từ Spring Security
     * @return SseEmitter để gửi events
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        logger.info("User {} connected to notification stream", user.getId());
        return notificationService.createEmitter(user.getId());
    }

    /**
     * Lấy tất cả thông báo của user hiện tại
     * 
     * GET /api/notifications
     * 
     * @param authentication Authentication object
     * @return Danh sách thông báo
     */
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAllNotifications(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<NotificationDTO> notifications = notificationService.getAllNotifications(user);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            logger.error("Error getting notifications", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lấy các thông báo chưa đọc
     * 
     * GET /api/notifications/unread
     * 
     * @param authentication Authentication object
     * @return Danh sách thông báo chưa đọc
     */
    @GetMapping("/unread")
    public ResponseEntity<Map<String, Object>> getUnreadNotifications(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            var unreadNotifications = notificationService.getUnreadNotifications(user);
            long unreadCount = notificationService.getUnreadCount(user);

            Map<String, Object> response = new HashMap<>();
            response.put("notifications", unreadNotifications);
            response.put("count", unreadCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting unread notifications", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lấy số lượng thông báo chưa đọc
     * 
     * GET /api/notifications/unread/count
     * 
     * @param authentication Authentication object
     * @return Số lượng thông báo chưa đọc
     */
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            long count = notificationService.getUnreadCount(user);

            Map<String, Long> response = new HashMap<>();
            response.put("count", count);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting unread count", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Đánh dấu một thông báo là đã đọc
     * 
     * PUT /api/notifications/{id}/read
     * 
     * @param id ID của thông báo
     * @return Response status
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable Long id) {
        try {
            notificationService.markAsRead(String.valueOf(id));

            Map<String, String> response = new HashMap<>();
            response.put("message", "Notification marked as read");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error marking notification as read", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Đánh dấu tất cả thông báo là đã đọc
     * 
     * PUT /api/notifications/read-all
     * 
     * @param authentication Authentication object
     * @return Response status
     */
    @PutMapping("/read-all")
    public ResponseEntity<Map<String, String>> markAllAsRead(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            notificationService.markAllAsRead(user);

            Map<String, String> response = new HashMap<>();
            response.put("message", "All notifications marked as read");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error marking all notifications as read", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gửi thông báo test (chỉ dùng cho development/testing)
     * 
     * POST /api/notifications/test
     * 
     * @param authentication Authentication object
     * @param payload        Nội dung thông báo test
     * @return Response status
     */
    @PostMapping("/test")
    public ResponseEntity<Map<String, String>> sendTestNotification(
            Authentication authentication,
            @RequestBody Map<String, String> payload) {
        try {
            User user = (User) authentication.getPrincipal();
            String message = payload.getOrDefault("message", "This is a test notification");

            notificationService.sendNotification(user, "TEST", message);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Test notification sent successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error sending test notification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lấy thông tin về SSE connections (admin/debug endpoint)
     * 
     * GET /api/notifications/connections/status
     * 
     * @param authentication Authentication object
     * @return Thông tin về connections
     */
    @GetMapping("/connections/status")
    public ResponseEntity<Map<String, Object>> getConnectionStatus(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();

            Map<String, Object> response = new HashMap<>();
            response.put("totalActiveConnections", notificationService.getActiveConnectionsCount());
            response.put("userHasActiveConnection", notificationService.hasActiveConnection(user.getId()));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting connection status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Đóng tất cả SSE connections của user (logout)
     * 
     * POST /api/notifications/disconnect
     * 
     * @param authentication Authentication object
     * @return Response status
     */
    @PostMapping("/disconnect")
    public ResponseEntity<Map<String, String>> disconnectUser(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            notificationService.closeUserConnections(user.getId());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Disconnected successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error disconnecting user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
