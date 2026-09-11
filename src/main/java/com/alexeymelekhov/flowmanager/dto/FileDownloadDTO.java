package com.alexeymelekhov.flowmanager.dto;

import org.springframework.core.io.Resource;

public record FileDownloadDTO(
        Resource resource,
        String fileName
) {
}
