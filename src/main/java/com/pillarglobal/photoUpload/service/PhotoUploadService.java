package com.pillarglobal.photoUpload.service;

import org.springframework.web.multipart.MultipartFile;

public interface PhotoUploadService {

    public void  uploadToLocal(MultipartFile multipartFile);
}
