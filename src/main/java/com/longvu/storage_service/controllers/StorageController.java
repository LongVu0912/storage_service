package com.longvu.storage_service.controllers;

import java.io.IOException;
import java.util.List;

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
import com.longvu.storage_service.entities.FileEntity;
import com.longvu.storage_service.exception.AppException;
import com.longvu.storage_service.services.StorageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class StorageController {
    @Autowired
    private StorageService fileService;

    @PostMapping("")
    public ResponseEntity<ApiResponse> uploadFile(@RequestParam("files") List<MultipartFile> files)
            throws AppException {
        String fileNameExists = fileService.fileNameExists(files);

        if (fileNameExists != "") {
            return ResponseEntity.ok()
                    .body(ApiResponse.builder()
                            .code(1000)
                            .message("Filename exists: " + fileNameExists)
                            .result(null)
                            .build());
        }

        for (MultipartFile file : files) {
            fileService.uploadFile(file);
        }

        return ResponseEntity.ok()
                .body(ApiResponse.builder()
                        .message("Upload Successfully")
                        .result(null)
                        .build());
    }

    @GetMapping("/getAllFiles")
    public ResponseEntity<ApiResponse> getAllFiles() {
        List<FileEntity> files = fileService.getAllFiles();

        return ResponseEntity.ok()
                .body(ApiResponse.builder()
                        .message("Get all files successfully")
                        .result(files)
                        .build());
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<byte[]> getFile(@PathVariable String fileName) throws IOException {
        byte[] file = fileService.getFileFromFilename(fileName);

        String contentType = fileService.getContentType(fileName);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(contentType))
                .body(file);
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<ApiResponse> deleteFile(@PathVariable String fileName) throws AppException {
        fileService.deleteFile(fileName);

        return ResponseEntity.ok()
                .body(ApiResponse.builder()
                        .message("Delete file successfully")
                        .result(null)
                        .build());
    }
}
