package com.alexeymelekhov.flowmanager.dto;

import com.alexeymelekhov.flowmanager.model.FileStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record FileConvertedEventDTO(
        @NotNull UUID eventId,
        @NotBlank String bucket,
        @NotNull FileStatus status,
        List<FileConvertedDTO> files
) {
}
