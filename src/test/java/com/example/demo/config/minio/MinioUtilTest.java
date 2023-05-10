package com.example.demo.config.minio;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MinioUtilTest {
    @Autowired
    private MinioUtil minioUtil;

    @Test
    void uploadFile() {
        MultipartFile temp = new MockMultipartFile("asset", "testAsset.glb", "model/gltf-binary", "123".getBytes());
        minioUtil.uploadFile(temp);
    }
}