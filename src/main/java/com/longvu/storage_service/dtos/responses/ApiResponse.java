package com.longvu.storage_service.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ApiResponse {
    @Builder.Default
    private int code = 200;

    private String message;

    private Object result;
}