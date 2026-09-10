package com.alexeymelekhov.flowmanager.repository;

import com.alexeymelekhov.flowmanager.model.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileRepository extends JpaRepository<File, UUID> {
}
