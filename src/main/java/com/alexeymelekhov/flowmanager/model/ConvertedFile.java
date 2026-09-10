package com.alexeymelekhov.flowmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "converted_files")
public class ConvertedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "file_id", nullable = false)
    private UUID fileId;

    public ConvertedFile(String name, UUID fileId) {
        this.name = name;
        this.fileId = fileId;
    }
}
