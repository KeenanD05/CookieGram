package com.group2.backend.service;

import com.group2.backend.payloads.AuthenticationResult;
import com.group2.backend.payloads.UserProfileResponse;
import com.group2.backend.security.request.LoginRequest;
import com.group2.backend.security.request.SignupRequest;
import com.group2.backend.security.request.StaffRegistrationRequest;
import com.group2.backend.security.response.MessageResponse;
import com.group2.backend.security.response.StaffResponse;
import com.group2.backend.security.response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;


public interface AuthService {
    ResponseEntity<AuthenticationResult> login(LoginRequest loginRequest);

    ResponseCookie logout();

    ResponseEntity<?> register(SignupRequest registerRequest);

    ResponseEntity<?> registerStaff(@Valid StaffRegistrationRequest staffRequest);


    UserProfileResponse getUserProfile(Long id);



}
