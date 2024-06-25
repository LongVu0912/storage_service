package com.longvu.storage_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_NAME_EXISTS(1000, "Filename exists", HttpStatus.OK),

    INVALID_USER(1001, "Invalid user", HttpStatus.OK),
    INVALID_FILE(1001, "Invalid file", HttpStatus.OK),
    INVALID_TOKEN(1001, "Invalid token", HttpStatus.OK),

    FILE_NAME_NOT_FOUND(4004, "File not found", HttpStatus.OK),
    FILE_NOT_FOUND(4004, "File not found", HttpStatus.OK),
    USER_NOT_FOUND(4004, "User not found", HttpStatus.OK),

    USERNAME_TAKEN(2000, "Username is already taken", HttpStatus.OK),
    WRONG_USERNAME_OR_PASSWORD(2001, "Wrong username or password", HttpStatus.OK),

    TOKEN_IS_REQUIRED(3000, "Token is required", HttpStatus.OK);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
