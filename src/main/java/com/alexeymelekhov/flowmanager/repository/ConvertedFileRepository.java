package com.alexeymelekhov.flowmanager.repository;

import com.alexeymelekhov.flowmanager.model.ConvertedFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConvertedFileRepository extends JpaRepository<ConvertedFile, Long> {

    List<ConvertedFile> findAllByFileId(UUID fileId);
}
