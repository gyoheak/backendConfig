package com.myserver.myApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.swagger.v3.oas.annotations.Operation;

@RestController
public class FileController {

    @Autowired
    private MinioClient minioClient;

    @PostMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "이미지 스냅샷 저장 메소드", description = "이미지 스냅샷을 저장하는 메소드입니다.")
    public String uploadFile(@RequestPart("asset") MultipartFile file) {
        try {
            if (minioClient
                    .bucketExists(BucketExistsArgs.builder().bucket("junman-test")
                            .build()) == false) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("junman-test").build());
            }
            ;
            minioClient.putObject(
                    PutObjectArgs.builder().bucket("junman-test").object(file.getOriginalFilename())
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            return "File uploaded successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "File uploaded failed";
        }
    }

    // @GetMapping("/files")
    // public List<String> getFiles() {
    // List<String> files = new ArrayList<>();
    // try {
    // Iterable<Result<Item>> results = minioClient.listObjects("mybucket");
    // for (Result<Item> result : results) {
    // Item item = result.get();
    // files.add(item.objectName());
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return files;
    // }

    // @GetMapping("/files/{filename}")
    // public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
    // try {
    // InputStream inputStream = minioClient.getObject("mybucket", filename);
    // Resource resource = new InputStreamResource(inputStream);
    // return
    // ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    // }

    // @DeleteMapping("/files/{filename}")
    // public String deleteFile(@PathVariable String filename) {
    // try {
    // minioClient.removeObject("mybucket", filename);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return "File deleted successfully";
    // }
}
