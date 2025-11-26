package com.group2.backend.controllers;

import com.group2.backend.model.User;
import com.group2.backend.payloads.AuthenticationResult;
import com.group2.backend.payloads.UserProfileResponse;
import com.group2.backend.repository.UserRepository;
import com.group2.backend.security.request.LoginRequest;
import com.group2.backend.security.request.SignupRequest;
import com.group2.backend.security.request.StaffRegistrationRequest;

import com.group2.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;



    @PostMapping("/auth/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest,
            @CookieValue(value = "sessionId", required = false) String guestSessionId) {
        // Authenticate the user
        ResponseEntity<AuthenticationResult> response = authService.login(loginRequest);
        AuthenticationResult result = response.getBody();

        // Clear the guest session cookie and set the JWT cookie
        ResponseCookie clearGuestCookie = ResponseCookie.from("sessionId", "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .build();
                
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, result.getJwtCookie().toString())
                .header(HttpHeaders.SET_COOKIE, clearGuestCookie.toString())
                .body(result.getResponse());
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout() {

        ResponseCookie authCookie = authService.logout();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, authCookie.toString()).body("You have been logged out");
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<?> signup(
            @Valid @RequestBody SignupRequest registerRequest,
            @CookieValue(value = "sessionId", required = false) String guestSessionId) {
        // Register the user
        ResponseEntity<?> response = authService.register(registerRequest);
        

        return response;
    }

    @PostMapping("/admin/register-staff")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerStaff(
            @Valid @RequestBody StaffRegistrationRequest staffRequest) {
        return authService.registerStaff(staffRequest);
    }

    @GetMapping("/auth/me")
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Get the user from the database by username
        User user = userRepository.findByUserName(username)
            .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
            
        UserProfileResponse profile = authService.getUserProfile(user.getId());
        return ResponseEntity.ok(profile);
    }

}
