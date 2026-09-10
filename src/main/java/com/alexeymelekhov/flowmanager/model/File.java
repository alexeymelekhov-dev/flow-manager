package com.alexeymelekhov.flowmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "files")
public class File {

    @Id
    private UUID id = UUID.randomUUID();

    @NotBlank
    @Size(min = 3, max = 255)
    private String name;

    @NotBlank
    @Size(min = 10, max = 2000)
    private String path;

    @Enumerated(EnumType.STRING)
    private FileStatus status;

    public File(String name, String path, FileStatus status) {
        this.name = name;
        this.path = path;
        this.status = status;
    }
}
