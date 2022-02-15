package com.pillarglobal.photoUpload.service.serviceImpl;

import com.pillarglobal.photoUpload.service.PhotoUploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class PhotoUploadServiceImpl implements PhotoUploadService {

    private String uploadPath = "/Users/mohammad.yasir/Documents/uploads/uploaded_";
    @Override
    public void uploadToLocal(MultipartFile multipartFile) {
        try{
            byte[] data = multipartFile.getBytes();
            Path path = Paths.get(uploadPath + multipartFile.getOriginalFilename());
            Files.write(path,data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
