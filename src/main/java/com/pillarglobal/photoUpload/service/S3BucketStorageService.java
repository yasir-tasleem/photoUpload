package com.pillarglobal.photoUpload.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.pillarglobal.photoUpload.model.PhotoInfo;
import com.pillarglobal.photoUpload.repository.PhotoRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class S3BucketStorageService {

    private final Logger logger = LoggerFactory.getLogger(S3BucketStorageService.class);

    @Autowired
    private AmazonS3 amazonS3Client;

    @Autowired
    private PhotoRepository photoRepository;

    private final Path basePath = Paths.get("/Users/mohammad.yasir/Documents/uploads");


    @Value("${application.bucket.name}")
    private String bucketName;

    private UUID photoGuid;

//    public String uploadFile(String keyName, MultipartFile file) {
//        try {
//            ObjectMetadata metadata = new ObjectMetadata();
//
//            if (!file.isEmpty() && (file.getContentType().equals("image/jpeg")) || (file.getContentType().equals("image/png")) || (file.getContentType().equals("image/jpg")) && file.getSize() <= 20000) {
//
//                photoGuid = java.util.UUID.randomUUID();
//                logger.info("photoGuid : " + photoGuid);
//                metadata.setContentLength(file.getSize());
//                metadata.setContentType(file.getContentType());
//                amazonS3Client.putObject(bucketName, String.valueOf(photoGuid), file.getInputStream(), metadata);
//                PhotoInfo photoInfo = new PhotoInfo();
//                photoInfo.setPhotoName(file.getOriginalFilename());
//                photoInfo.setPhotoGuid(String.valueOf(photoGuid));
//                photoRepository.save(photoInfo);
//
//                return "File uploaded: " + photoGuid;
//            } else throw new Exception("Image not accepted");
//        } catch (IOException ioe) {
//            logger.error("IOException: " + ioe.getMessage());
//        } catch (AmazonServiceException serviceException) {
//            logger.info("AmazonServiceException: " + serviceException.getMessage());
//            throw serviceException;
//        } catch (AmazonClientException clientException) {
//            logger.info("AmazonClientException Message: " + clientException.getMessage());
//            throw clientException;
//        } catch (Exception e) {
//            logger.error("IOException: " + e.getMessage());
//            return "File not uploaded: " + e.getMessage();
//        }
//        return "File not uploaded: " + keyName;
//    }

    public Mono<String> uploadUsingMono(Mono<FilePart> filePartMono) {
        try {
            photoGuid = java.util.UUID.randomUUID();
            return filePartMono.flatMap(fp ->
                            fp.transferTo(basePath.resolve(photoGuid + "." + FilenameUtils.getExtension(fp.filename()))).thenReturn(fp))
                    .map(res ->
                            saveToS3(photoGuid + "." + FilenameUtils.getExtension(res.filename()), res.filename()));
        } catch (Exception e) {
            return Mono.error(new IOException());
        }
    }

    @Async
    public String saveToS3(String fileName, String orgFileName) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            File file = createFileObject(basePath + "/" + fileName);
            FileInputStream fileInputStream = new FileInputStream(file);

            final Tika tika = new Tika();
            String type = tika.detect(file);
            long size = Files.size(Paths.get(basePath + "/" + fileName));

            if (type.equals("image/jpeg") || type.equals("image/png") || type.equals("image/jpg") && size <= 20000) {
                metadata.setContentLength(file.length());
                PhotoInfo photoInfo = new PhotoInfo();
                photoInfo.setPhotoName(orgFileName);
                photoInfo.setPhotoGuid(String.valueOf(photoGuid));
                photoRepository.save(photoInfo);
                amazonS3Client.putObject(bucketName, String.valueOf(photoGuid), fileInputStream, metadata);
                amazonS3Client.setObjectAcl(bucketName, String.valueOf(photoGuid), CannedAccessControlList.PublicRead);
                URL s3Url = amazonS3Client.getUrl(bucketName, String.valueOf(photoGuid));
                return "File uploaded :" + fileName + " and URL is:" + s3Url;
            } else {
                Files.delete(Paths.get(basePath + "/" + fileName));
                logger.info("Image not accepted");
                return "Only Image having PNG/JPEG/JPG format and size less than 20 mb accepted ";
            }
        } catch (AmazonServiceException serviceException) {
            logger.info("AmazonServiceException: " + serviceException.getMessage());
            throw serviceException;
        } catch (AmazonClientException clientException) {
            logger.info("AmazonClientException Message: " + clientException.getMessage());
            throw clientException;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception: " + e.getMessage());
        }
        return "File not uploaded";
    }

    public File createFileObject(String path){
        return new File(path);
    }
}