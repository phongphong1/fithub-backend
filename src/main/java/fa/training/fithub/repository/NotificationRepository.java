package fa.training.fithub.repository;

import fa.training.fithub.entity.Notification;
import fa.training.fithub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Lấy tất cả thông báo chưa đọc của user
     */
    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);

    /**
     * Lấy tất cả thông báo của user (đã đọc và chưa đọc)
     */
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    /**
     * Đếm số thông báo chưa đọc
     */
    long countByUserAndIsReadFalse(User user);

    /**
     * Đánh dấu tất cả thông báo của user là đã đọc
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user = :user AND n.isRead = false")
    void markAllAsReadByUser(@Param("user") User user);

    /**
     * Xóa các thông báo cũ hơn một khoảng thời gian (tùy chọn)
     */
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoffTime")
    void deleteOldNotifications(@Param("cutoffTime") LocalDateTime cutoffTime);
}
