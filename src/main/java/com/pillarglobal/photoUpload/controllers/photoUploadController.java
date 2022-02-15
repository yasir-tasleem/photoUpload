package com.pillarglobal.photoUpload.controllers;

import com.pillarglobal.photoUpload.service.PhotoUploadService;
import com.pillarglobal.photoUpload.service.S3BucketStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("api/v1")
public class photoUploadController {

    @Autowired
    private PhotoUploadService photoUploadService;

    @Autowired
    S3BucketStorageService service;

    @PostMapping("upload/local")
    public void uploadLocally(@RequestParam("file")MultipartFile multipartFile){
        photoUploadService.uploadToLocal(multipartFile);

    }

    @PostMapping("/upload/aws")
    public ResponseEntity<String> uploadFile(@RequestParam("fileName") String fileName,
                                             @RequestParam("file") MultipartFile file) {
        return new ResponseEntity<>(service.uploadFile(fileName, file), HttpStatus.OK);
    }
}
