package fa.training.fithub.dto.response;

import fa.training.fithub.enums.Gender;
import fa.training.fithub.enums.Role;
import fa.training.fithub.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private Role role;
    private Gender gender;
    private UserStatus status;
    private String avatarUrl;
    private String coverUrl;
    private String bio;
    private LocalDate dateOfBirth;
    private LocalDateTime lastLoginAt;
}
