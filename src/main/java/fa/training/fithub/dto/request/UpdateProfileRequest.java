package fa.training.fithub.dto.request;

import fa.training.fithub.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {
    @NotBlank(message = "Full name cannot be blank")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Full name can only contain letters and spaces")
    private String fullName;

    @Past(message = "Date of birth must be a date in the past")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender cannot be blank")
    private Gender gender;

    @Size(max = 255, message = "Avatar URL cannot exceed 255 characters")
    private String avatarUrl;

    @Size(max = 255, message = "Cover URL cannot exceed 255 characters")
    private String coverUrl;

    @Size(max = 1500, message = "Bio cannot exceed 1500 characters")
    private String bio;
}

