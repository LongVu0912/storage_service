package com.longvu.storage_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_NAME_EXISTS(1000, "Filename exists", HttpStatus.CONFLICT),
    INVALID_FILE(1001, "Invalid file", HttpStatus.BAD_REQUEST),
    FILE_NAME_NOT_FOUND(1002, "File not found", HttpStatus.NOT_FOUND),
    FILE_NOT_FOUND(1003, "File not found", HttpStatus.NOT_FOUND),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
