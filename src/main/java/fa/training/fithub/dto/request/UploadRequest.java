package fa.training.fithub.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO for file upload request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadRequest {
    @NotNull(message = "File is required")
    private MultipartFile file;
    private String folder;
    private String customFileName;
}
