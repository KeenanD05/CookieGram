package com.group2.backend.payloads;

import com.group2.backend.security.response.JwtResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseCookie;

@Data
@AllArgsConstructor
public class AuthenticationResult {
    private final JwtResponse response;
    private final ResponseCookie jwtCookie;
}
