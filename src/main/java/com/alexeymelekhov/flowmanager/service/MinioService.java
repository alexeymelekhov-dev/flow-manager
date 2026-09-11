package com.alexeymelekhov.flowmanager.service;

import com.alexeymelekhov.flowmanager.exception.ErrorMessage;
import com.alexeymelekhov.flowmanager.exception.FileStorageException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    public String upload(InputStream fileStream, String fileName) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(fileStream, -1, 10 * 1024 * 1024)
                            .build()
            );

            return bucket + "/" + fileName;
        } catch (Exception e) {
            log.info(e.getMessage());

            throw new FileStorageException(
                    ErrorMessage.FAILED_UPLOAD_FILE.getMessage(), e
            );
        }
    }

    public InputStream download(String bucket, String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            throw new FileStorageException(ErrorMessage.FAILED_DOWNLOAD_FILE.getMessage(), e);
        }
    }
}