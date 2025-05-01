package com.cloudify.demologin.util;

import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Component
public class MinioUtil {

    private final MinioClient minioClient;

    public MinioUtil(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Value("${minio.url}")
    private String minioUrl;

    public String uploadFile(String bucketName, String fileName, byte[] file) throws Exception {
        boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!isExist) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }

        try (InputStream is = new ByteArrayInputStream(file)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName + ".jpg")
                            .stream(is, file.length, -1)
                            .contentType("image/jpeg")
                            .build()
            );
        }
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
