package com.longvu.storage_service.dtos.responses;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class LoginResponse {
    private String id;

    private String token;
}
