package com.pillarglobal.photoUpload.controllers;

import com.pillarglobal.photoUpload.service.PhotoUploadService;
import com.pillarglobal.photoUpload.service.S3BucketStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
@RequestMapping("api/v1")
public class photoUploadController {

    @Autowired
    private PhotoUploadService photoUploadService;

    @Autowired
    S3BucketStorageService service;

    private final Path basePath = Paths.get("/Users/mohammad.yasir/Documents/uploads/");

    @PostMapping("upload/local")
    public void uploadLocally(@RequestParam("file")MultipartFile multipartFile){
        photoUploadService.uploadToLocal(multipartFile);

    }

    @PostMapping("/upload/aws")
    public ResponseEntity<String> uploadFile(@RequestParam("fileName") String fileName,
                                             @RequestParam("file") MultipartFile file) {
        return new ResponseEntity<>(service.uploadFile(fileName, file), HttpStatus.OK);
    }

    // use Flux<FilePart> for multiple file upload
    @PostMapping(value = "upload/single", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Void> upload(@RequestPart("fileToUpload") Mono<FilePart> filePartMono){
//        return new ResponseEntity<>(service.uploadUsingMono(filePartMono), HttpStatus.OK);
        try{
            return  filePartMono
                    .doOnNext(fp -> System.out.println("Received File : " + fp.filename()))
                    .flatMap(fp -> fp.transferTo(basePath.resolve(fp.filename())))
                    .then();

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return Mono.empty();
    }
}
