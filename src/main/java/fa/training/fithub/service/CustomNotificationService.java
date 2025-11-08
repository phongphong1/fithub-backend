package fa.training.fithub.service;

import fa.training.fithub.entity.User;

public interface CustomNotificationService {
    void sendSimpleNotification(User user);
    void sendLikeNotification(User postOwner, User liker, Long postId);
    void sendCommentNotification(User postOwner, User commenter, Long postId, String commentContent);
    void sendCourseEnrollmentNotification(User user, Long courseId, String courseName);
    void sendLessonCompleteNotification(User user, Long lessonId, String lessonName, String courseName);
    void sendCommentReplyNotification(User originalCommenter, User replier, Long commentId, String replyContent);
    void sendSystemAnnouncementToAll(Iterable<User> users, String announcement);
    void sendTrainerApplicationToAdmin(User user);

}
