package fa.training.fithub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for upload configuration response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadConfigResponse {

    // Config version for cache invalidation
    private String configVersion;
    
    // Image configurations
    private List<String> allowedImageFormats;
    private Double maxImageUploadSizeMb;

    // Video configurations
    private List<String> allowedVideoFormats;
    private Double maxVideoUploadSizeMb;

    // Document configurations
    private List<String> allowedDocumentFormats;
    private Double maxDocumentUploadSizeMb;

    // Trainer-specific configurations
    private Integer maxTrainerDocuments;
}
