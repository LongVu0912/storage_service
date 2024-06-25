package com.longvu.storage_service.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.longvu.storage_service.dtos.requests.LoginRequest;
import com.longvu.storage_service.dtos.requests.RegisterRequest;
import com.longvu.storage_service.dtos.responses.IntrospectResponse;
import com.longvu.storage_service.dtos.responses.LoginResponse;
import com.longvu.storage_service.dtos.responses.TokenRequest;
import com.longvu.storage_service.entities.InvalidatedTokenEntity;
import com.longvu.storage_service.entities.UserEntity;
import com.longvu.storage_service.exception.AppException;
import com.longvu.storage_service.exception.ErrorCode;
import com.longvu.storage_service.repositories.InvalidatedTokenRepository;
import com.longvu.storage_service.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final InvalidatedTokenRepository invalidatedTokenRepository;

    public void register(RegisterRequest newUser) throws AppException {
        Optional<UserEntity> userOptional = userRepository.findByUsername(newUser.getUsername());
        if (userOptional.isPresent()) {
            throw new AppException(ErrorCode.USERNAME_TAKEN);
        }
        try {
            userRepository.save(UserEntity.builder()
                    .username(newUser.getUsername())
                    .password(passwordEncoder.encode(newUser.getPassword()))
                    .fullName(newUser.getFullname())
                    .build());
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public LoginResponse login(LoginRequest loginRequest) throws AppException {
        LoginResponse loginResponse = null;
        try {
            Optional<UserEntity> userOptional = userRepository.findByUsername(loginRequest.getUsername());
            if (userOptional.isPresent()) {
                UserEntity user = userOptional.get();
                if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                    String jwtToken;
                    jwtToken = jwtService.generateToken(user);
                    loginResponse = LoginResponse.builder()
                            .id(user.getId())
                            .token(jwtToken)
                            .build();
                }
            }
        } catch (AppException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        if (loginResponse == null) {
            throw new AppException(ErrorCode.WRONG_USERNAME_OR_PASSWORD);
        }
        return loginResponse;
    }

    public void logout(String token) throws AppException {
        try {
            var signedToken = jwtService.verifyToken(token);

            String jit = signedToken.getJWTClaimsSet().getJWTID();

            Date expirationTime = signedToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedTokenEntity invalidatedToken = InvalidatedTokenEntity.builder()
                    .token(jit)
                    .expirationTime(expirationTime)
                    .build();
            invalidatedTokenRepository.save(invalidatedToken);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public IntrospectResponse introspect(TokenRequest request) throws AppException {
        var token = request.getToken();

        if (token == null) {
            throw new AppException(ErrorCode.TOKEN_IS_REQUIRED);
        }

        return jwtService.introspect(token);
    }
}
