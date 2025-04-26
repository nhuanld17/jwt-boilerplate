package com.example.Boilerplate_JWTBasedAuthentication.dto.respone;

import com.example.Boilerplate_JWTBasedAuthentication.entity.Role;
import lombok.*;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String username;
    private Set<Role> roles;
    private String token;
    private String refreshToken;
}
