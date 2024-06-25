package com.longvu.storage_service.dtos.responses;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileResponse {
    private String id;
    private String fileName;
    private String fileType;
    private String filePath;
}
