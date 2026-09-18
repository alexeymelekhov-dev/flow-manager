package com.alexeymelekhov.flowmanager.exception;

import lombok.Getter;

@Getter
public enum ErrorMessage {

    FAILED_UPLOAD_FILE("Failed to upload file"),
    EMPTY_FILE_NAME("The file name is missing"),
    EMPTY_FILE("The file cannot be empty"),
    FAILED_SERIALIZE_EVENT("Failed to serialize event"),
    FAILED_DESERIALIZE_EVENT("Failed to deserialize Kafka message"),
    FAILED_PUBLISH_EVENT("Failed to publish event: "),
    FILE_NOT_FOUND("File not found"),
    FILE_NOT_READY_TO_DOWNLOAD("File is not ready for download"),
    FAILED_DOWNLOAD_FILE("Failed to download file from MinIO"),
    FAILED_TO_CREATE_ZIP("Failed to create ZIP archive"),
    FAILED_MAX_FILE_SIZE("File size exceeds the allowed limit for your subscription"),
    INTERNAL_SERVER_ERROR("Internal server error");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return message.formatted(args);
    }

}
