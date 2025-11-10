package fa.training.fithub.dto.request;

import fa.training.fithub.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessApplicationRequest {

    @NotNull(message = "Status không được để trống")
    private ApplicationStatus status; // APPROVED hoặc REJECTED

    // Admin feedback: 
    // - REQUIRED khi REJECTED
    // - OPTIONAL khi APPROVED
    @Size(max = 1000, message = "Admin feedback không được vượt quá 1000 ký tự")
    private String adminFeedback;

}

