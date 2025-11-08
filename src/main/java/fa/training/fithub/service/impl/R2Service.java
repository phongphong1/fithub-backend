package fa.training.fithub.service.impl;

import fa.training.fithub.service.CloudService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class R2Service implements CloudService {

    private static final Logger logger = LoggerFactory.getLogger(R2Service.class);


    private final S3Client s3Client;

    @Value("${r2.bucket.name}")
    private String bucketName;


    /**
     * Upload file to R2 bucket
     * @param file
     * @param key
     * @return
     */
    @Override
    public String uploadFile(MultipartFile file, String key) throws IOException {
        logger.info("Uploading file: {} to R2 bucket: {}", key, bucketName);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        logger.info("File uploaded successfully: {}", key);
        return key;
    }

    /**
     * Download a file from R2
     * @param key
     * @return
     */
    @Override
    public ResponseInputStream<GetObjectResponse> downloadFile(String key) {
        logger.info("Downloading file: {} from R2 bucket: {}", key, bucketName);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        return s3Client.getObject(getObjectRequest);
    }

    /**
     * Delete a file from R2
     * @param key
     */
    @Override
    public void deleteFile(String key) {
        logger.info("Deleting file: {} from R2 bucket: {}", key, bucketName);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
        logger.info("File deleted successfully: {}", key);
    }

    /**
     * Check if a file exists in R2
     * @param key
     * @return
     */
    @Override
    public boolean fileExists(String key) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }
}
