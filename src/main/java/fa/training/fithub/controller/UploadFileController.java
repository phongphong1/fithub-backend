package fa.training.fithub.controller;

import fa.training.fithub.dto.response.ApiResponse;
import fa.training.fithub.dto.response.UploadResponse;
import fa.training.fithub.exception.BadRequestException;
import fa.training.fithub.service.CloudService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Controller for handling file upload operations using CloudService (R2)
 */
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadFileController {

    private static final Logger logger = LoggerFactory.getLogger(UploadFileController.class);

    private final CloudService cloudService;

    @Value("${r2.public.endpoint}")
    private String publicEndpoint;

    // Allowed file types
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/gif",
            "image/webp",
            "application/pdf",
            "video/mp4",
            "video/mpeg",
            "video/quicktime");

    // Max file size: 100MB
    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024;

    /**
     * Upload a single file
     *
     * @param file           the file to upload
     * @param folder         optional folder path (e.g., "trainers/certificates")
     * @param customFileName optional custom file name (without extension)
     * @return ApiResponse containing file URL and details
     */
    @PostMapping("/single")
    public ResponseEntity<ApiResponse<UploadResponse>> uploadSingleFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", required = false) String folder,
            @RequestParam(value = "customFileName", required = false) String customFileName) {

        logger.info("Received upload request for file: {}", file.getOriginalFilename());

        try {
            // Validate file
            validateFile(file);

            // Generate unique key
            String key = generateFileKey(file, folder, customFileName);

            // Upload to R2
            cloudService.uploadFile(file, key);

            // Generate public URL
            String url = generatePublicUrl(key);

            // Build response
            UploadResponse response = UploadResponse.builder()
                    .key(key)
                    .url(url)
                    .originalFilename(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .uploadedAt(System.currentTimeMillis())
                    .build();

            logger.info("File uploaded successfully: {} -> {}", file.getOriginalFilename(), url);

            return ResponseEntity.ok(ApiResponse.<UploadResponse>builder()
                    .success(true)
                    .data(response)
                    .message("File uploaded successfully")
                    .build());

        } catch (BadRequestException e) {
            logger.error("Validation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<UploadResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());

        } catch (IOException e) {
            logger.error("Failed to upload file: {}", file.getOriginalFilename(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<UploadResponse>builder()
                            .success(false)
                            .message("Failed to upload file: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Upload multiple files
     *
     * @param files  list of files to upload
     * @param folder optional folder path
     * @return ApiResponse containing list of file URLs and details
     */
    @PostMapping("/multiple")
    public ResponseEntity<ApiResponse<List<UploadResponse>>> uploadMultipleFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "folder", required = false) String folder) {

        logger.info("Received upload request for {} files", files.size());

        try {
            List<UploadResponse> responses = new ArrayList<>();

            for (MultipartFile file : files) {
                // Validate file
                validateFile(file);

                // Generate unique key
                String key = generateFileKey(file, folder, null);

                // Upload to R2
                cloudService.uploadFile(file, key);

                // Generate public URL
                String url = generatePublicUrl(key);

                // Build response
                UploadResponse response = UploadResponse.builder()
                        .key(key)
                        .url(url)
                        .originalFilename(file.getOriginalFilename())
                        .fileSize(file.getSize())
                        .contentType(file.getContentType())
                        .uploadedAt(System.currentTimeMillis())
                        .build();

                responses.add(response);
                logger.info("File uploaded: {} -> {}", file.getOriginalFilename(), url);
            }

            logger.info("Successfully uploaded {} files", responses.size());

            return ResponseEntity.ok(ApiResponse.<List<UploadResponse>>builder()
                    .success(true)
                    .data(responses)
                    .message(responses.size() + " file(s) uploaded successfully")
                    .build());

        } catch (BadRequestException e) {
            logger.error("Validation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<List<UploadResponse>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());

        } catch (IOException e) {
            logger.error("Failed to upload files", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<UploadResponse>>builder()
                            .success(false)
                            .message("Failed to upload files: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Delete a file
     *
     * @param key the key/path of the file to delete
     * @return ApiResponse confirming deletion
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteFile(@RequestParam("key") String key) {
        logger.info("Received delete request for file: {}", key);

        try {
            cloudService.deleteFile(key);

            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("File deleted successfully")
                    .build());

        } catch (Exception e) {
            logger.error("Failed to delete file: {}", key, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Failed to delete file: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Check if a file exists
     *
     * @param key the key/path of the file
     * @return ApiResponse with boolean result
     */
    @GetMapping("/exists")
    public ResponseEntity<ApiResponse<Boolean>> fileExists(@RequestParam("key") String key) {
        logger.info("Checking if file exists: {}", key);

        boolean exists = cloudService.fileExists(key);

        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .success(true)
                .data(exists)
                .message(exists ? "File exists" : "File not found")
                .build());
    }

    /**
     * Validate file before upload
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty or null");
        }

        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds maximum limit of " + (MAX_FILE_SIZE / 1024 / 1024) + "MB");
        }

        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException(
                    "File type not allowed: " + contentType + ". Allowed types: " + ALLOWED_CONTENT_TYPES);
        }

        logger.debug("File validation passed: {}", file.getOriginalFilename());
    }

    /**
     * Generate unique file key for storage
     */
    private String generateFileKey(MultipartFile file, String folder, String customFileName) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName;
        if (customFileName != null && !customFileName.trim().isEmpty()) {
            fileName = customFileName.trim();
        } else {
            fileName = UUID.randomUUID().toString();
        }

        String key;
        if (folder != null && !folder.trim().isEmpty()) {
            // Remove leading/trailing slashes
            folder = folder.trim().replaceAll("^/+|/+$", "");
            key = folder + "/" + fileName + extension;
        } else {
            key = fileName + extension;
        }

        logger.debug("Generated file key: {}", key);
        return key;
    }

    /**
     * Generate public URL for accessing the file
     */
    private String generatePublicUrl(String key) {
        String url = publicEndpoint + "/" + key;
        logger.debug("Generated public URL: {}", url);
        return url;
    }
}
