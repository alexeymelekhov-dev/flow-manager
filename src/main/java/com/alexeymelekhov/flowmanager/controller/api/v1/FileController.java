package com.alexeymelekhov.flowmanager.controller.api.v1;

import com.alexeymelekhov.flowmanager.dto.FileDTO;
import com.alexeymelekhov.flowmanager.dto.FileDownloadDTO;
import com.alexeymelekhov.flowmanager.dto.FileStatusDTO;
import com.alexeymelekhov.flowmanager.service.FileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    private final FileService fileService;

    @PostMapping
    public ResponseEntity<FileDTO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("X-User-Login") String login
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fileService.upload(file, login));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<FileStatusDTO> getStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(fileService.getStatus(id));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable UUID id) {
        FileDownloadDTO fileDownloadDTO = fileService.download(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileDownloadDTO.fileName() + "\"")
                .body(fileDownloadDTO.resource());
    }
}
