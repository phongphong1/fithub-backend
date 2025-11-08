package fa.training.fithub.dto.response;

import fa.training.fithub.enums.Gender;
import fa.training.fithub.enums.Role;
import fa.training.fithub.enums.UserStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseFullFieldDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String avatarUrl;
    private String coverUrl;
    private String bio;
    private Role role;
    private UserStatus status;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
