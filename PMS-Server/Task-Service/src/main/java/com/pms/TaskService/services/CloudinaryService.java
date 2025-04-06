package com.pms.TaskService.services;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for uploading files to Cloudinary or similar cloud storage.
 */
public interface CloudinaryService {

    /**
     * Uploads an image file to the cloud storage and returns its URL.
     *
     * @param file The image file to be uploaded.
     * @return The URL of the uploaded image.
     */
    String uploadImage(MultipartFile file);
}
