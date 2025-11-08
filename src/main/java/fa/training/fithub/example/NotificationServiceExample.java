package fa.training.fithub.example;

import fa.training.fithub.entity.User;
import fa.training.fithub.service.impl.SystemNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Ví dụ về cách sử dụng SystemNotificationService
 * 
 * File này chỉ để tham khảo, không được sử dụng trong production
 */
@Component
@RequiredArgsConstructor
public class NotificationServiceExample {

    private final SystemNotificationService notificationService;

    /**
     * Ví dụ 1: Gửi thông báo đơn giản
     */
    public void sendSimpleNotification(User user) {
        notificationService.sendNotification(
                user,
                "Welcome",
                "Chào mừng bạn đến với FitHub!");
    }

    /**
     * Ví dụ 2: Gửi thông báo khi có người like post
     */
    public void sendLikeNotification(User postOwner, User liker, Long postId) {
        Map<String, String> customData = new HashMap<>();
        customData.put("type", "LIKE");
        customData.put("referenceId", String.valueOf(postId));
        customData.put("referenceType", "post");
        customData.put("senderId", String.valueOf(liker.getId()));

        String message = String.format("%s đã thích bài viết của bạn", liker.getFullName());

        notificationService.sendPushNotification(
                postOwner,
                "New Like",
                message,
                customData);
    }

    /**
     * Ví dụ 3: Gửi thông báo khi có comment mới
     */
    public void sendCommentNotification(User postOwner, User commenter, Long postId, String commentContent) {
        Map<String, String> customData = new HashMap<>();
        customData.put("type", "COMMENT");
        customData.put("referenceId", String.valueOf(postId));
        customData.put("referenceType", "post");
        customData.put("senderId", String.valueOf(commenter.getId()));

        String message = String.format(
                "%s đã bình luận: \"%s\"",
                commenter.getFullName(),
                commentContent.length() > 50 ? commentContent.substring(0, 50) + "..." : commentContent);

        notificationService.sendPushNotification(
                postOwner,
                "New Comment",
                message,
                customData);
    }

    /**
     * Ví dụ 4: Gửi thông báo đăng ký khóa học thành công
     */
    public void sendCourseEnrollmentNotification(User user, Long courseId, String courseName) {
        Map<String, String> customData = new HashMap<>();
        customData.put("type", "COURSE_ENROLL");
        customData.put("referenceId", String.valueOf(courseId));
        customData.put("referenceType", "course");

        String message = String.format(
                "Bạn đã đăng ký thành công khóa học \"%s\"",
                courseName);

        notificationService.sendPushNotification(
                user,
                "Course Enrollment Success",
                message,
                customData);
    }

    /**
     * Ví dụ 5: Gửi thông báo hoàn thành bài học
     */
    public void sendLessonCompleteNotification(User user, Long lessonId, String lessonName, String courseName) {
        Map<String, String> customData = new HashMap<>();
        customData.put("type", "LESSON_COMPLETE");
        customData.put("referenceId", String.valueOf(lessonId));
        customData.put("referenceType", "lesson");

        String message = String.format(
                "Chúc mừng! Bạn đã hoàn thành bài học \"%s\" trong khóa học \"%s\"",
                lessonName,
                courseName);

        notificationService.sendPushNotification(
                user,
                "Lesson Completed",
                message,
                customData);
    }

    /**
     * Ví dụ 6: Gửi thông báo reply comment
     */
    public void sendCommentReplyNotification(User originalCommenter, User replier, Long commentId,
            String replyContent) {
        Map<String, String> customData = new HashMap<>();
        customData.put("type", "COMMENT_REPLIED");
        customData.put("referenceId", String.valueOf(commentId));
        customData.put("referenceType", "comment");
        customData.put("senderId", String.valueOf(replier.getId()));

        String message = String.format(
                "%s đã trả lời bình luận của bạn: \"%s\"",
                replier.getFullName(),
                replyContent.length() > 50 ? replyContent.substring(0, 50) + "..." : replyContent);

        notificationService.sendPushNotification(
                originalCommenter,
                "Comment Reply",
                message,
                customData);
    }

    /**
     * Ví dụ 7: Gửi thông báo hệ thống đến tất cả users
     */
    public void sendSystemAnnouncementToAll(Iterable<User> users, String announcement) {
        for (User user : users) {
            Map<String, String> customData = new HashMap<>();
            customData.put("type", "SYSTEM_NOTIFICATION");

            notificationService.sendPushNotification(
                    user,
                    "System Announcement",
                    announcement,
                    customData);
        }
    }

    /**
     * Ví dụ 8: Kiểm tra user có đang online không
     */
    public boolean isUserOnline(Long userId) {
        return notificationService.hasActiveConnection(userId);
    }

    /**
     * Ví dụ 9: Lấy số lượng thông báo chưa đọc
     */
    public long getUnreadCount(User user) {
        return notificationService.getUnreadCount(user);
    }

    /**
     * Ví dụ 10: Đóng tất cả connections của user (khi logout)
     */
    public void logoutUser(Long userId) {
        notificationService.closeUserConnections(userId);
    }
}
