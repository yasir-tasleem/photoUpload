package com.pillarglobal.photoUpload.service;

import com.amazonaws.services.s3.AmazonS3;
import com.pillarglobal.photoUpload.repository.PhotoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

@ExtendWith(SpringExtension.class)
@WebFluxTest(S3BucketStorageService.class)
class S3BucketStorageServiceTest {


    S3BucketStorageService s3BucketStorageService = new S3BucketStorageService();

    @MockBean
    private AmazonS3 amazonS3Client;

    @MockBean
    private PhotoRepository photoRepository;


    @Autowired
    private WebTestClient webClient;


    @Test
    void uploadUsingMonoForPNG() throws IOException {

        FilePart filePart = new FilePart() {
            @Override
            public String filename() {
                return "abc.jpeg";
            }

            @Override
            public Mono<Void> transferTo(Path dest) {
                return Mono.empty();
            }

            @Override
            public String name() {
                return null;
            }

            @Override
            public HttpHeaders headers() {
                return null;
            }

            @Override
            public Flux<DataBuffer> content() {
                return null;
            }
        };
        Mono<FilePart> mono = Mono.just(filePart);
        File file = new File("/Users/mohammad.yasir/Documents/uploads/8a478e8c-e144-451d-b973-c20a1a82f068.png");
        S3BucketStorageService spy = Mockito.spy(new S3BucketStorageService());
        spy.photoRepository = photoRepository;
        spy.amazonS3Client = amazonS3Client;
        Mockito.doReturn(file).when(spy).createFileObject(Mockito.anyString());
        Mockito.doReturn(Mockito.mock(FileInputStream.class)).when(spy).createFileInputObject(Mockito.any());
        Mockito.doReturn(1024L).when(spy).getFileSize(Mockito.any());
        StepVerifier.create(spy.uploadUsingMono(mono))
                .expectNextMatches(s -> s.contains("File uploaded :")).verifyComplete();

    }

    @Test
    void uploadUsingMonoForJPG() throws IOException {

        FilePart filePart = new FilePart() {
            @Override
            public String filename() {
                return "abc.jpeg";
            }

            @Override
            public Mono<Void> transferTo(Path dest) {
                return Mono.empty();
            }

            @Override
            public String name() {
                return null;
            }

            @Override
            public HttpHeaders headers() {
                return null;
            }

            @Override
            public Flux<DataBuffer> content() {
                return null;
            }
        };
        Mono<FilePart> mono = Mono.just(filePart);
        File file = new File("/Users/mohammad.yasir/Documents/uploads/3b1b9f7d-66d1-4f5d-a291-a70bc30f05f0.jpg");
        S3BucketStorageService spy = Mockito.spy(new S3BucketStorageService());
        spy.photoRepository = photoRepository;
        spy.amazonS3Client = amazonS3Client;
        Mockito.doReturn(file).when(spy).createFileObject(Mockito.anyString());
        Mockito.doReturn(Mockito.mock(FileInputStream.class)).when(spy).createFileInputObject(Mockito.any());
        Mockito.doReturn(1024L).when(spy).getFileSize(Mockito.any());
        StepVerifier.create(spy.uploadUsingMono(mono)).
                .expectNextMatches(s -> s.contains("File uploaded :")).verifyComplete();

    }

    @Test
    void uploadUsingMonoWithException() {
        FilePart filePart = Mockito.mock(FilePart.class);
        Mono<FilePart> mono = Mono.just(filePart);

        StepVerifier.create(s3BucketStorageService.uploadUsingMono(Mono.error(new IOException("File Not Found"))))
                .expectErrorMessage("File Not Found").verify();
    }
}