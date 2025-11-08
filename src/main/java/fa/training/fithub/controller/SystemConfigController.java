package fa.training.fithub.controller;

import fa.training.fithub.dto.response.ApiResponse;
import fa.training.fithub.dto.response.UploadConfigResponse;
import fa.training.fithub.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * Controller for system configuration
 */
@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
public class SystemConfigController {

        private static final Logger logger = LoggerFactory.getLogger(SystemConfigController.class);

        private final SystemConfigService systemConfigService;

        // Default values for images
        private static final List<String> DEFAULT_IMAGE_FORMATS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
        private static final double DEFAULT_MAX_IMAGE_SIZE_MB = 5.0;

        // Default values for videos
        private static final List<String> DEFAULT_VIDEO_FORMATS = Arrays.asList("mp4", "mpeg", "quicktime", "avi");
        private static final double DEFAULT_MAX_VIDEO_SIZE_MB = 50.0;

        // Default values for documents
        private static final List<String> DEFAULT_DOCUMENT_FORMATS = Arrays.asList("pdf", "doc", "docx");
        private static final double DEFAULT_MAX_DOCUMENT_SIZE_MB = 10.0;

        // Default values for trainer
        private static final int DEFAULT_MAX_TRAINER_DOCUMENTS = 10;

        /**
         * Get upload configuration
         * 
         * @return Upload configuration including max file sizes, allowed formats for
         *         different file types
         */
        @GetMapping("/upload")
        public ResponseEntity<ApiResponse<UploadConfigResponse>> getUploadConfig() {
                logger.info("Fetching upload configuration");

                try {
                        // Get config version for cache invalidation
                        String configVersion = systemConfigService.getString("upload_config_version", "1.0");

                        // Get image configurations
                        String imageFormatsStr = systemConfigService.getString("allowed_image_formats", "");
                        List<String> allowedImageFormats = imageFormatsStr.isEmpty()
                                        ? DEFAULT_IMAGE_FORMATS
                                        : Arrays.asList(imageFormatsStr.split(","));

                        Double maxImageUploadSizeMb = systemConfigService.getDouble("max_image_upload_size_mb",
                                        DEFAULT_MAX_IMAGE_SIZE_MB);

                        // Get video configurations
                        String videoFormatsStr = systemConfigService.getString("allowed_video_formats", "");
                        List<String> allowedVideoFormats = videoFormatsStr.isEmpty()
                                        ? DEFAULT_VIDEO_FORMATS
                                        : Arrays.asList(videoFormatsStr.split(","));

                        Double maxVideoUploadSizeMb = systemConfigService.getDouble("max_video_upload_size_mb",
                                        DEFAULT_MAX_VIDEO_SIZE_MB);

                        // Get document configurations
                        String documentFormatsStr = systemConfigService.getString("allowed_document_formats", "");
                        List<String> allowedDocumentFormats = documentFormatsStr.isEmpty()
                                        ? DEFAULT_DOCUMENT_FORMATS
                                        : Arrays.asList(documentFormatsStr.split(","));

                        Double maxDocumentUploadSizeMb = systemConfigService.getDouble("max_document_upload_size_mb",
                                        DEFAULT_MAX_DOCUMENT_SIZE_MB);

                        // Get trainer-specific configurations
                        Integer maxTrainerDocuments = systemConfigService.getInteger("max_trainer_documents",
                                        DEFAULT_MAX_TRAINER_DOCUMENTS);

                        UploadConfigResponse config = UploadConfigResponse.builder()
                                        .configVersion(configVersion)
                                        .allowedImageFormats(allowedImageFormats)
                                        .maxImageUploadSizeMb(maxImageUploadSizeMb)
                                        .allowedVideoFormats(allowedVideoFormats)
                                        .maxVideoUploadSizeMb(maxVideoUploadSizeMb)
                                        .allowedDocumentFormats(allowedDocumentFormats)
                                        .maxDocumentUploadSizeMb(maxDocumentUploadSizeMb)
                                        .maxTrainerDocuments(maxTrainerDocuments)
                                        .build();

                        logger.info("Upload config retrieved: version={}, maxDocumentSize={}MB, maxTrainerDocuments={}",
                                        configVersion, maxDocumentUploadSizeMb, maxTrainerDocuments);

                        return ResponseEntity.ok(ApiResponse.<UploadConfigResponse>builder()
                                        .success(true)
                                        .data(config)
                                        .message("Upload configuration retrieved successfully")
                                        .build());

                } catch (Exception e) {
                        logger.error("Failed to get upload configuration", e);

                        // Return default config on error
                        UploadConfigResponse defaultConfig = UploadConfigResponse.builder()
                                        .configVersion("1.0")
                                        .allowedImageFormats(DEFAULT_IMAGE_FORMATS)
                                        .maxImageUploadSizeMb(DEFAULT_MAX_IMAGE_SIZE_MB)
                                        .allowedVideoFormats(DEFAULT_VIDEO_FORMATS)
                                        .maxVideoUploadSizeMb(DEFAULT_MAX_VIDEO_SIZE_MB)
                                        .allowedDocumentFormats(DEFAULT_DOCUMENT_FORMATS)
                                        .maxDocumentUploadSizeMb(DEFAULT_MAX_DOCUMENT_SIZE_MB)
                                        .maxTrainerDocuments(DEFAULT_MAX_TRAINER_DOCUMENTS)
                                        .build();

                        return ResponseEntity.ok(ApiResponse.<UploadConfigResponse>builder()
                                        .success(true)
                                        .data(defaultConfig)
                                        .message("Using default upload configuration")
                                        .build());
                }
        }
}
