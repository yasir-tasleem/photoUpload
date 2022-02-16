package com.pillarglobal.photoUpload.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3BucketStorageService {

    private Logger logger = LoggerFactory.getLogger(S3BucketStorageService.class);

    @Autowired
    private AmazonS3 amazonS3Client;

    @Value("${application.bucket.name}")
    private String bucketName;

    private UUID photoGuid;

    public String uploadFile(String keyName, MultipartFile file)  {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            logger.info("file.getSize() :" + file.getContentType() );
            logger.info("file.getContentType() :" + file.getContentType());
            if(!file.isEmpty() && (file.getContentType().equals("image/jpeg"))
                    || (file.getContentType().equals("image/png"))
                    || (file.getContentType().equals("image/jpg"))
                    && file.getSize() <= 20000){

                photoGuid = java.util.UUID.randomUUID();
                logger.info("photoGuid : " + photoGuid);
                metadata.setContentLength(file.getSize());
                amazonS3Client.putObject(bucketName, String.valueOf(photoGuid), file.getInputStream(), metadata);
                logger.info(String.valueOf(amazonS3Client.getUrl("bucket","hi")));
                return "File uploaded: " + photoGuid;
            }
            else throw new Exception("Image not accepted");
           } catch (IOException ioe) {
            logger.error("IOException: " + ioe.getMessage());
        } catch (AmazonServiceException serviceException) {
            logger.info("AmazonServiceException: " + serviceException.getMessage());
            throw serviceException;
        } catch (AmazonClientException clientException) {
            logger.info("AmazonClientException Message: " + clientException.getMessage());
            throw clientException;
        } catch (Exception e) {
            logger.error("IOException: " + e.getMessage());
            return "File not uploaded: " + e.getMessage();
        }
        return "File not uploaded: " + keyName;
    }
}