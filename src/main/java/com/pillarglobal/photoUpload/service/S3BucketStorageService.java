package com.pillarglobal.photoUpload.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.pillarglobal.photoUpload.model.PhotoInfo;
import com.pillarglobal.photoUpload.repository.PhotoRepository;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class S3BucketStorageService {

    private Logger logger = LoggerFactory.getLogger(S3BucketStorageService.class);

    @Autowired
    private AmazonS3 amazonS3Client;

    @Autowired
    private PhotoRepository photoRepository;


    @Value("${application.bucket.name}")
    private String bucketName;

    private UUID photoGuid;

    public String uploadFile(String keyName, MultipartFile file)  {
        try {
            ObjectMetadata metadata = new ObjectMetadata();

            if(!file.isEmpty() && (file.getContentType().equals("image/jpeg"))
                    || (file.getContentType().equals("image/png"))
                    || (file.getContentType().equals("image/jpg"))
                    && file.getSize() <= 20000){

                photoGuid = java.util.UUID.randomUUID();
                logger.info("photoGuid : " + photoGuid);
                metadata.setContentLength(file.getSize());
                metadata.setContentType(file.getContentType());
                amazonS3Client.putObject(bucketName, String.valueOf(photoGuid), file.getInputStream(), metadata);
                PhotoInfo photoInfo = new PhotoInfo();
                photoInfo.setPhotoName(file.getOriginalFilename());
                photoInfo.setPhotoGuid(String.valueOf(photoGuid));
                photoRepository.save(photoInfo);

                logger.info(String.valueOf(amazonS3Client.getUrl("bucket","hi")));
//                s3Client.putObject(new PutObjectRequest("your-bucket", "some-path/PutObjectRequestsome-key.jpg", new File("somePath/someKey.jpg")).withCannedAcl(CannedAccessControlList.PublicRead))
//                s3Client.getResourceUrl("your-bucket", "some-path/some-key.jpg");
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

//    public String uploadUsingMono(Mono<FilePart> filePartMono){
//        try{
//            ObjectMetadata objectMetadata = new ObjectMetadata();
//            photoGuid = java.util.UUID.randomUUID();
//            logger.info("photoGuid : " + photoGuid);
//                    filePartMono
//                    .flatMap(fp -> fp.transferTo(Paths.get(fp.filename())))
//                    .then();
//            amazonS3Client.putObject(bucketName,String.valueOf(photoGuid),filePartMono,objectMetadata);
//            return "File uploaded: " + photoGuid;
//
//        }catch (Exception e){
//            logger.error("IOException: " + e.getMessage());
//            return "File not uploaded: " + e.getMessage();
//        }
//    }

}