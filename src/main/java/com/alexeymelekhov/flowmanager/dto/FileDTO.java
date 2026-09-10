package com.alexeymelekhov.flowmanager.dto;

import com.alexeymelekhov.flowmanager.model.FileStatus;

public record FileDTO(
        String name,
        FileStatus status
) {
}
