package fa.training.fithub.service.impl;

import fa.training.fithub.entity.User;
import fa.training.fithub.enums.Role;
import fa.training.fithub.repository.UserRepository;
import fa.training.fithub.service.CustomNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomNotificationServiceImpl implements CustomNotificationService {

    private final SystemNotificationService notificationService;
    private final UserRepository userRepository;

    @Override
    public void sendSimpleNotification(User user) {

    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public void sendCommentReplyNotification(User originalCommenter, User replier, Long commentId, String replyContent) {
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

    @Override
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

    @Override
    public void sendTrainerApplicationToAdmin(User user) {
        Iterable<User> admins = userRepository.findByRole(Role.ADMIN);

        String trainerName = user.getFullName();
        String announcement = String.format("Trainer %s has submitted an application to become a trainer. Please review and approve.", trainerName);

        for (User admin : admins) {
            Map<String, String> customData = new HashMap<>();
            customData.put("type", "SYSTEM_NOTIFICATION");

            notificationService.sendPushNotification(
                    admin,
                    "Trainer Application",
                    announcement,
                    customData);
        }

    }
}
