package fa.training.fithub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for trainer application request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerApplicationRequest {

    @NotBlank(message = "Qualifications are required")
    private String qualifications;

    @NotBlank(message = "Experience details are required")
    private String experience_details;

    @NotEmpty(message = "At least one certificate URL is required")
    private List<String> certificateUrls;
}
