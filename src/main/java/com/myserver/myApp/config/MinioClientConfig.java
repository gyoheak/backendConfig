package com.myserver.myApp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

@Configuration
public class MinioClientConfig {

    @Value("${minio.uri}")
    private String minioUrl;

    @Value("${minio.access-key}")
    private String minioAccessKey;

    @Value("${minio.secret-key}")
    private String minioSecretKey;

    @Bean
    public MinioClient minioClient(){
        System.out.println("minioUrl: " + minioUrl);
        return MinioClient.builder()
        .endpoint(minioUrl)
        .credentials(minioAccessKey, minioSecretKey)
        .build();
    }
}
