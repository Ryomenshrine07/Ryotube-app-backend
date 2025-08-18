package com.ryotube.application.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;

@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;

    private final S3Presigner presigner;
    @Autowired
    public S3Service(S3Presigner presigner) {
        this.presigner = presigner;
    }

    @Value("${aws.region}")
    private String awsRegion;

    public String uploadFile(MultipartFile file, String bucketName) throws IOException {
        String uniqueFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, awsRegion, uniqueFileName);
    }
    public String generatePresignedUrl(String videoKey, String videoBucketName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(videoBucketName)
                .key(videoKey)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .getObjectRequest(getObjectRequest)
                .build();

        return presigner.presignGetObject(presignRequest).url().toString();
    }
}