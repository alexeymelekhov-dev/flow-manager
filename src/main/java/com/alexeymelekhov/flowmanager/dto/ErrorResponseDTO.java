package com.alexeymelekhov.flowmanager.dto;

import java.util.Map;

public record ErrorResponseDTO(
        int status,
        String message,
        Map<String, String> errors
) {
}
