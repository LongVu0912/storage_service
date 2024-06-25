package com.longvu.storage_service.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.longvu.storage_service.dtos.responses.FileResponse;
import com.longvu.storage_service.entities.FileEntity;
import com.longvu.storage_service.entities.UserEntity;
import com.longvu.storage_service.exception.AppException;
import com.longvu.storage_service.exception.ErrorCode;
import com.longvu.storage_service.repositories.StorageRepository;
import com.longvu.storage_service.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@EnableMethodSecurity()
@RequiredArgsConstructor
public class StorageService {
    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private UserRepository userRepository;

    private final String FOLDER_PATH = determineFolderPath();

    public List<FileResponse> getMyFiles() {
        UserEntity currentUser = getCurrentUser();

        List<FileEntity> files = storageRepository.getByUser(currentUser);

        return files.stream()
                .map(file -> FileResponse.builder()
                        .id(file.getId())
                        .fileName(file.getFileName())
                        .filePath(file.getFilePath())
                        .fileType(file.getFileType())
                        .build())
                .collect(Collectors.toList());
    }

    public void uploadFile(MultipartFile fileUpload) throws AppException {
        UserEntity currentUser = getCurrentUser();

        if (fileUpload.getOriginalFilename() == null) {
            throw new AppException(ErrorCode.INVALID_FILE);
        }

        try {
            String filePath = FOLDER_PATH + fileUpload.getOriginalFilename();

            fileUpload.transferTo(new File(filePath));

            FileEntity newFile = FileEntity.builder()
                    .user(currentUser)
                    .fileName(fileUpload.getOriginalFilename())
                    .fileType(fileUpload.getContentType())
                    .filePath(filePath)
                    .build();

            storageRepository.save(newFile);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public void deleteFile(String fileName) throws AppException {
        UserEntity currentUser = getCurrentUser();

        Optional<FileEntity> fileData = storageRepository.findByFileName(fileName);

        if (currentUser != fileData.get().getUser()) {
            throw new AppException(ErrorCode.INVALID_USER);
        }

        String filePath = fileData.get().getFilePath();
        File file = new File(filePath);

        storageRepository.delete(fileData.get());

        if (file.exists()) {
            file.delete();
        }
    }

    public byte[] getFileFromFilename(String fileName) throws AppException {
        // UserEntity currentUser = getCurrentUser();

        Optional<FileEntity> fileData = storageRepository.findByFileName(fileName);

        //        if (fileData.get().getUser() != currentUser) {
        //            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        //        }

        String filePath = fileData.get().getFilePath();
        byte[] file = null;
        try {
            file = Files.readAllBytes(new File(filePath).toPath());
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }
        return file;
    }

    public String getContentType(String fileName) throws IOException {
        Optional<FileEntity> fileData = storageRepository.findByFileName(fileName);
        return fileData.get().getFileType();
    }

    public String fileNameExists(List<MultipartFile> files) {
        String fileNameExists = "";

        for (MultipartFile file : files) {
            if (storageRepository.existsByFileName(file.getOriginalFilename())) {
                fileNameExists += file.getOriginalFilename() + ", ";
            }
        }
        return fileNameExists;
    }

    private String determineFolderPath() {
        String folderPath = "";
        try {
            // Get the URL of the JAR file
            String jarPath = StorageService.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath();
            // Decode the path to handle spaces and special characters
            String decodedPath = java.net.URLDecoder.decode(jarPath, java.nio.charset.StandardCharsets.UTF_8);
            File jarFile = new File(decodedPath);
            // Get the parent directory of the JAR file
            String parentDir = jarFile.getParent();
            if (parentDir != null) {
                folderPath = parentDir + File.separator;
            } else {
                // Fallback to current working directory if the parent directory is not found
                folderPath = Paths.get("").toAbsolutePath().toString() + File.separator;
            }
        } catch (Exception e) {
            // Handle any exceptions (e.g., URISyntaxException) and fallback to current
            // directory
            folderPath = Paths.get("").toAbsolutePath().toString() + File.separator;
        }

        // Append "files" only once to the base path
        folderPath += "files" + File.separator;

        // Ensure the "files" folder exists
        File filesFolder = new File(folderPath);
        if (!filesFolder.exists()) {
            filesFolder.mkdirs(); // Create the folder if it does not exist
        }

        return filesFolder.getAbsolutePath() + File.separator;
    }

    private UserEntity getCurrentUser() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        Optional<UserEntity> user = userRepository.findByUsername(name);

        if (user.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        return user.get();
    }
}
