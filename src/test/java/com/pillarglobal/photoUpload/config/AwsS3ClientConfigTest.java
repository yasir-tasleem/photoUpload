package com.pillarglobal.photoUpload.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
class AwsS3ClientConfigTest {

    @Autowired
    AwsS3ClientConfig awsS3ClientConfig;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Test
    public void testAwsS3ClientConfig() {
        assertEquals(String.valueOf(awsS3ClientConfig.s3client().getRegion()), region);
    }
}