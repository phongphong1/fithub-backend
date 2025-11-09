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
    @NotBlank(message = "Full name không được để trống")
    @Size(min = 2, max = 100, message = "Full name phải từ 2 đến 100 ký tự")
    @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Full name chỉ chứa chữ cái và khoảng trắng")
    private String fullName;

    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    private LocalDate dateOfBirth;

    @NotNull(message = "Giới tính không được để trống")
    private Gender gender;

    @Size(max = 255, message = "Avatar URL không được vượt quá 255 ký tự")
    private String avatarUrl;

    @Size(max = 255, message = "Cover URL không được vượt quá 255 ký tự")
    private String coverUrl;

    @Size(max = 1500, message = "Bio không được vượt quá 1500 ký tự")
    private String bio;
}

