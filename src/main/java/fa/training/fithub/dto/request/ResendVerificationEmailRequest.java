package fa.training.fithub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResendVerificationEmailRequest {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email is invalid")
    private String email;
}
