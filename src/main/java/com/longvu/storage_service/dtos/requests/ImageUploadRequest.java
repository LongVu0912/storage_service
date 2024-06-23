package com.longvu.storage_service.dtos.requests;

import org.springframework.web.multipart.MultipartFile;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageUploadRequest {
    private MultipartFile fileUpload;
}