package com.project.ecommerce.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.project.ecommerce.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.folder:ecommerce}")
    private String folder;

    /**
     * Upload an image to Cloudinary.
     *
     * @param file multipart image file
     * @return secure (https) URL of uploaded image
     */
    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidCredentialsException("Image file is required");
        }
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new InvalidCredentialsException("Only image files are allowed");
        }

        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "image"
                    )
            );

            Object secureUrl = uploadResult.get("secure_url");
            if (secureUrl == null) {
                // Fallback to non-https if needed
                secureUrl = uploadResult.get("url");
            }
            if (secureUrl == null) {
                throw new InvalidCredentialsException("Upload succeeded but no URL returned from Cloudinary");
            }
            return secureUrl.toString();
        } catch (IOException e) {
            throw new InvalidCredentialsException("Failed to read image bytes: " + e.getMessage());
        } catch (Exception e) {
            throw new InvalidCredentialsException("Failed to upload image to Cloudinary: " + e.getMessage());
        }
    }

    public void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            // Log the error but don't throw an exception since deletion is not critical
            System.err.println("Failed to delete image from Cloudinary: " + e.getMessage());
        }
    }
}
