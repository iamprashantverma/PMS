package com.pms.TaskService.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.pms.TaskService.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    /* upload image on to the Cloud */
    public String  uploadImage(MultipartFile file) {
        try {
            Map params = ObjectUtils.asMap("folder", "UserService");
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
            return (String) uploadResult.get("url");
        } catch (IOException e) {
            log.error(" got error while uploading image,{}",String.valueOf(e));
        }
        return null;
    }

}



