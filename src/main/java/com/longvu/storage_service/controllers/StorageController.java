package com.longvu.storage_service.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.longvu.storage_service.dtos.responses.ApiResponse;
import com.longvu.storage_service.dtos.responses.FileResponse;
import com.longvu.storage_service.exception.AppException;
import com.longvu.storage_service.services.StorageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class StorageController {
    @Autowired
    private StorageService storageService;

    @PostMapping("")
    public ResponseEntity<ApiResponse> uploadFile(@RequestParam("files") List<MultipartFile> files)
            throws AppException {
        String fileNameExists = storageService.fileNameExists(files);

        if (!Objects.equals(fileNameExists, "")) {
            return ResponseEntity.ok()
                    .body(ApiResponse.builder()
                            .code(1000)
                            .message("Filename exists: " + fileNameExists)
                            .result(null)
                            .build());
        }

        for (MultipartFile file : files) {
            storageService.uploadFile(file);
        }

        return ResponseEntity.ok()
                .body(ApiResponse.builder()
                        .message("Upload Successfully")
                        .result(null)
                        .build());
    }

    @GetMapping("/getMyFiles")
    public ResponseEntity<ApiResponse> getAllFiles() {
        List<FileResponse> files = storageService.getMyFiles();

        return ResponseEntity.ok()
                .body(ApiResponse.builder()
                        .message("Get all files successfully")
                        .result(files)
                        .build());
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<byte[]> getFile(@PathVariable String fileName) throws IOException {
        byte[] file = storageService.getFileFromFilename(fileName);

        String contentType = storageService.getContentType(fileName);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(contentType))
                .body(file);
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<ApiResponse> deleteFile(@PathVariable String fileName) throws AppException {
        storageService.deleteFile(fileName);

        return ResponseEntity.ok()
                .body(ApiResponse.builder()
                        .message("Delete file successfully")
                        .result(null)
                        .build());
    }
}
