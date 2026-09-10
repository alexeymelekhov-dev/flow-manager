package com.alexeymelekhov.flowmanager.dto;

import jakarta.validation.constraints.NotBlank;

public record FileConvertedDTO(
        @NotBlank String name
) {
}
