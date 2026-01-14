package com.ryotube.application.Services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    // Upload image or video file (legacy - loads into memory)
    public Map<String, Object> uploadFile(MultipartFile file, String folderName) throws IOException {
        @SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "resource_type", "auto",
                        "folder", folderName
                )
        );
        return uploadResult;
    }

    // Stream upload video - handles Cloudinary free tier 100MB limit
    @SuppressWarnings("unchecked")
    public Map<String, Object> uploadVideoStream(InputStream inputStream, String folderName, String originalFilename) throws IOException {
        // Cloudinary SDK doesn't accept InputStream directly, so we use a temp file
        File tempFile = File.createTempFile("upload_", getExtension(originalFilename));
        try {
            // Copy stream to temp file
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            long fileSizeBytes = tempFile.length();
            long cloudinaryLimit = 100 * 1024 * 1024; // 100MB Cloudinary free tier limit
            
            if (fileSizeBytes > cloudinaryLimit) {
                // File exceeds Cloudinary free tier limit
                throw new IOException(String.format(
                    "Video file size (%.1f MB) exceeds Cloudinary free tier limit (100 MB). " +
                    "Please compress your video or upgrade your Cloudinary plan.",
                    fileSizeBytes / (1024.0 * 1024.0)
                ));
            }
            
            // Use chunked upload for all videos for reliability
            Map<String, Object> uploadResult = cloudinary.uploader().uploadLarge(
                    tempFile,
                    ObjectUtils.asMap(
                            "resource_type", "video",
                            "folder", folderName,
                            "chunk_size", 6000000, // 6MB chunks
                            "public_id", originalFilename != null ? originalFilename.replaceAll("\\.[^.]+$", "") : null
                    )
            );
            return uploadResult;
        } finally {
            // Clean up temp file
            tempFile.delete();
        }
    }

    // Stream upload image - uses temp file for Cloudinary compatibility
    @SuppressWarnings("unchecked")
    public Map<String, Object> uploadImageStream(InputStream inputStream, String folderName) throws IOException {
        File tempFile = File.createTempFile("upload_", ".tmp");
        try {
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    tempFile,
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "folder", folderName
                    )
            );
            return uploadResult;
        } finally {
            tempFile.delete();
        }
    }

    // Delete a file using its public_id
    @SuppressWarnings("unchecked")
    public Map<String, Object> deleteFile(String publicId) throws IOException {
        return cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "auto"));
    }
    
    private String getExtension(String filename) {
        if (filename == null) return ".tmp";
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot) : ".tmp";
    }
}
