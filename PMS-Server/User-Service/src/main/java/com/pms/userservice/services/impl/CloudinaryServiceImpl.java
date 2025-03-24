package com.pms.userservice.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService implements com.pms.userservice.services.CloudinaryService {

    private final Cloudinary cloudinary;

    /* upload image on to the Cloud */
    public String  uploadImage(MultipartFile file) throws IOException {
        Map params = ObjectUtils.asMap("folder", "BookHive");
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        return (String) uploadResult.get("url");
    }


}



