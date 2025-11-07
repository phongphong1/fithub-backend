package fa.training.fithub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for file upload response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadResponse {

    private String key;

    private String url;

    private String originalFilename;

    private Long fileSize;

    private String contentType;

    private Long uploadedAt;
}
