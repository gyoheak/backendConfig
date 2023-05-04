package com.example.demo.service.minio;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
class MinioTestTest {
    @Autowired
    private MinioService minioTest;

    @Test
    void uploadFile() {
         MultipartFile temp = new MockMultipartFile("asset", "testAsset.glb", "model/gltf-binary", "123".getBytes());
         minioTest.uploadFile(temp);
    }
}