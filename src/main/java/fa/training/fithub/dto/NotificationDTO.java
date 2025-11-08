package fa.training.fithub.dto;

import fa.training.fithub.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private Long userId;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private NotificationType type;
    private Long referenceId;
    private String referenceType;
    private String content;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
