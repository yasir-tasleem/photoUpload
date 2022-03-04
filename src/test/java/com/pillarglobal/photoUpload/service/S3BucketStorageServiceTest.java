package com.pillarglobal.photoUpload.service;

import com.amazonaws.services.s3.AmazonS3;
import com.pillarglobal.photoUpload.config.AwsS3ClientConfig;
import com.pillarglobal.photoUpload.repository.PhotoRepository;
import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    void uploadUsingMono() {

        //FilePart filePart = Mockito.mock(FilePart.class);
        //Mockito.when(filePart.filename()).thenReturn("abc.jpeg");
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

        //Mockito.when(s3BucketStorageService.createFileObject(Mockito.anyString())).thenReturn(file);
        //s3BucketStorageService.uploadUsingMono(mono);
        StepVerifier.create(s3BucketStorageService.uploadUsingMono(mono))
                .expectNextMatches(s-> s.contains("File uploaded :")).verifyComplete();

    }

    @Test
    void uploadUsingMonoWithException() {
        FilePart filePart = Mockito.mock(FilePart.class);
        Mono<FilePart> mono = Mono.just(filePart);

        StepVerifier.create(s3BucketStorageService.uploadUsingMono(Mono.error(new IOException("File Not Found"))))
                .expectErrorMessage("File Not Found").verify();
    }
}