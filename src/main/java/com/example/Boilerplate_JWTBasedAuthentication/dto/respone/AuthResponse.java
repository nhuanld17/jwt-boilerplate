package com.example.Boilerplate_JWTBasedAuthentication.dto.respone;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String refreshToken;
}
