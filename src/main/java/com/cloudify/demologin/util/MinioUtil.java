package com.cloudify.demologin.util;

import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MinioUtil {

    private final MinioClient minioClient;

    public MinioUtil(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Value("${minio.url}")
    private String minioUrl;

    public String uploadFile(String bucketName, String fileName, MultipartFile file) throws Exception {
        boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!isExist) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName + ".jpg")
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

        return buildPublicUrl(bucketName, fileName);
    }

    public void deleteFile(String bucketName, String fileName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName + ".jpg")
                            .build()
            );
        } catch (Exception ignored) {
        }
    }

    private String buildPublicUrl(String bucketName, String fileName) {
        return minioUrl + "/" + bucketName + "/" + fileName + ".jpg";
    }
}
