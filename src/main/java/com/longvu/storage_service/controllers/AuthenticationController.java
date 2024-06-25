package com.longvu.storage_service.controllers;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.longvu.storage_service.dtos.requests.LoginRequest;
import com.longvu.storage_service.dtos.requests.RegisterRequest;
import com.longvu.storage_service.dtos.responses.ApiResponse;
import com.longvu.storage_service.dtos.responses.LoginResponse;
import com.longvu.storage_service.dtos.responses.TokenRequest;
import com.longvu.storage_service.exception.AppException;
import com.longvu.storage_service.services.AuthenticationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest newUser) throws AppException {
        authenticationService.register(newUser);

        return ResponseEntity.ok()
                .body(ApiResponse.builder()
                        .message("Register successfully")
                        .result(null)
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest loginRequest) throws AppException {
        LoginResponse loginResponse = authenticationService.login(loginRequest);

        return ResponseEntity.ok()
                .body(ApiResponse.builder()
                        .message("Login successfully")
                        .result(loginResponse)
                        .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestBody TokenRequest logoutRequest) throws AppException {
        authenticationService.logout(logoutRequest.getToken());

        return ResponseEntity.ok()
                .body(ApiResponse.builder()
                        .message("Logout successfully")
                        .result(null)
                        .build());
    }
}
