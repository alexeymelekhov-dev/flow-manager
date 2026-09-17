package com.alexeymelekhov.flowmanager.service;

import com.alexeymelekhov.flowmanager.client.SubscriptionClient;
import com.alexeymelekhov.flowmanager.dto.*;
import com.alexeymelekhov.flowmanager.exception.*;
import com.alexeymelekhov.flowmanager.model.*;
import com.alexeymelekhov.flowmanager.repository.ConvertedFileRepository;
import com.alexeymelekhov.flowmanager.repository.FileRepository;
import com.alexeymelekhov.flowmanager.repository.InboxRepository;
import com.alexeymelekhov.flowmanager.repository.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final MinioService minioService;
    private final OutboxRepository outboxRepository;
    private final InboxRepository inboxRepository;
    private final ObjectMapper objectMapper;
    private final ConvertedFileRepository convertedFileRepository;
    private final SubscriptionClient subscriptionClient;

    @Value("${spring.file.upload.max-size}")
    private DataSize maxFileSize;

    @Value("${spring.kafka.producer.topic}")
    private String topic;

    @Value("${minio.bucket}")
    private String bucket;

    private static final String CONVERTED_FILES_ZIP = "converted-files.zip";

    @Transactional
    public FileDTO upload(MultipartFile file, String login) {
        if (file.isEmpty()) {
            throw new FileValidationException(ErrorMessage.EMPTY_FILE.getMessage());
        }

        SubscriptionDTO subscription = subscriptionClient.getSubscription(login);

        if (subscription.type() == SubscriptionType.FREE
            && file.getSize() > maxFileSize.toBytes()) {
            throw new FileValidationException(ErrorMessage.FAILED_MAX_FILE_SIZE.getMessage());
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new FileValidationException(ErrorMessage.EMPTY_FILE_NAME.getMessage());
        }

        String path;
        try (InputStream inputStream = file.getInputStream()) {
            path = minioService.upload(inputStream, originalName);
        } catch (IOException e) {
            throw new FileStorageException(ErrorMessage.FAILED_UPLOAD_FILE.getMessage(), e);
        }

        File savedFile = saveFile(file, path);

        saveOutbox(savedFile);

        return new FileDTO(savedFile.getName(), savedFile.getStatus());
    }

    @Transactional
    public void handleFileConverted(FileConvertedEventDTO dto) {
        if (isAlreadyProcessed(dto.eventId())) {
            return;
        }

        File file = fileRepository.findById(dto.eventId())
                .orElseThrow(() -> new FileNotFoundException(ErrorMessage.FILE_NOT_FOUND.getMessage()));
        file.setStatus(dto.status());

        dto.files().forEach(f -> {
            convertedFileRepository.save(new ConvertedFile(f.name(), file.getId()));
        });

        saveInbox(dto.eventId());
    }

    public FileStatusDTO getStatus(UUID id) {
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException(ErrorMessage.FILE_NOT_FOUND.getMessage()));

        return new FileStatusDTO(file.getStatus());
    }

    public FileDownloadDTO download(UUID id) {
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException(ErrorMessage.FILE_NOT_FOUND.getMessage()));

        List<ConvertedFile> files = convertedFileRepository.findAllByFileId(id);

        if (files.isEmpty() || file.getStatus() != FileStatus.CONVERTED) {
            throw new FileNotReadyException(ErrorMessage.FILE_NOT_READY_TO_DOWNLOAD.getMessage());
        }

        if (files.size() == 1) {
            Resource downloadedFile = downloadSingleFile(files.getFirst(), file.getPath());

            return new FileDownloadDTO(downloadedFile, files.getFirst().getName());
        }

        Resource downloadedFilesZip = downloadAsZip(files, file.getPath());

        return new FileDownloadDTO(downloadedFilesZip, CONVERTED_FILES_ZIP);
    }

    private Resource downloadSingleFile(ConvertedFile file, String path) {
        String bucket = path.substring(0, path.indexOf('/'));
        InputStream inputStream = minioService.download(bucket, file.getName());

        return new InputStreamResource(inputStream);
    }

    public Resource downloadAsZip(List<ConvertedFile> files, String path) {
        String bucket = path.substring(0, path.indexOf('/'));

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {

                for (ConvertedFile file : files) {
                    InputStream inputStream = minioService.download(bucket, file.getName());

                    zipOutputStream.putNextEntry(new ZipEntry(file.getName()));

                    inputStream.transferTo(zipOutputStream);

                    zipOutputStream.closeEntry();
                    inputStream.close();
                }
            }

            return new ByteArrayResource(outputStream.toByteArray());

        } catch (IOException e) {
            throw new FileDownloadException(ErrorMessage.FAILED_TO_CREATE_ZIP.getMessage(), e);
        }
    }

    private File saveFile(MultipartFile file, String path) {
        File newFile = new File(
                file.getOriginalFilename(),
                path,
                FileStatus.PROCESSING
        );

        return fileRepository.save(newFile);
    }

    private void saveOutbox(File file) {
        FileUploadedEventDTO fileUploadedEventDTO = new FileUploadedEventDTO(
                file.getId(),
                bucket,
                file.getName()
        );

        Outbox outbox = new Outbox();
        outbox.setId(UUID.randomUUID());
        outbox.setEventId(fileUploadedEventDTO.eventId());
        outbox.setTopic(topic);
        outbox.setPayload(serialize(fileUploadedEventDTO));
        outbox.setCreatedAt(OffsetDateTime.now());

        outboxRepository.save(outbox);
    }

    private String serialize(FileUploadedEventDTO event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(ErrorMessage.FAILED_SERIALIZE_EVENT.getMessage(), e);
        }
    }

    private boolean isAlreadyProcessed(UUID eventId) {
        return inboxRepository.existsById(eventId);
    }

    private void saveInbox(UUID eventId) {
        inboxRepository.save(new Inbox(eventId));
    }
}
