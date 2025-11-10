package fa.training.fithub.dto.response;

import fa.training.fithub.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerApplicationResponse {

    private Long id;
    private Long userId;
    private String qualifications;
    private String experienceDetails;
    private Map<String, Object> documentUrls;
    private ApplicationStatus status;
    private String adminFeedback;
    private Instant createdAt;
    private Instant updatedAt;

    // User info (for admin view)
    private UserBasicInfo user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserBasicInfo {
        private Long id;
        private String username;
        private String fullName;
        private String email;
        private String avatarUrl;
    }

}

