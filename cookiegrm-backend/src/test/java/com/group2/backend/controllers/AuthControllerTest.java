package com.group2.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.backend.model.User;
import com.group2.backend.payloads.AuthenticationResult;
import com.group2.backend.security.response.JwtResponse;
import com.group2.backend.payloads.UserProfileResponse;
import com.group2.backend.repository.UserRepository;
import com.group2.backend.security.jwt.JwtUtils;
import com.group2.backend.security.request.LoginRequest;
import com.group2.backend.security.request.SignupRequest;
import com.group2.backend.security.request.StaffRegistrationRequest;
import com.group2.backend.security.response.MessageResponse;
import com.group2.backend.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private LoginRequest loginRequest;
    private SignupRequest signupRequest;
    private StaffRegistrationRequest staffRequest;
    private JwtResponse jwtResponse;
    private ResponseCookie jwtCookie;
    private AuthenticationResult authResult;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();


        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        signupRequest = new SignupRequest();
        signupRequest.setUsername("testuser");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setFirstName("Test");
        signupRequest.setLastName("User");
        signupRequest.setMobileNumber("1234567890");

        staffRequest = new StaffRegistrationRequest();
        staffRequest.setUsername("staffuser");
        staffRequest.setEmail("staff@example.com");
        staffRequest.setPassword("password123");
        staffRequest.setFirstName("Staff");
        staffRequest.setLastName("User");
        staffRequest.setMobileNumber("9876543210");

        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        jwtResponse = new JwtResponse(
                1L,
                "testuser",
                "test@example.com",
                "Test",
                "User",
                roles
        );

        jwtCookie = ResponseCookie.from("jwt", "test-jwt-token").build();
        authResult = new AuthenticationResult(jwtResponse, jwtCookie);
    }

    @Test
    void login_WithValidCredentials_ReturnsJwt() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(ResponseEntity.ok(authResult));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("jwt=test-jwt-token")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void login_WithInvalidCredentials_ReturnsUnauthorized() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Error: Invalid username or password"));
    }

    @Test
    void logout_ShouldClearJwtCookie() throws Exception {
        ResponseCookie cookie = ResponseCookie.from("jwt", "").maxAge(0).build();
        when(authService.logout()).thenReturn(cookie);

        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("jwt=;")));

        verify(authService, times(1)).logout();
    }

    @Test
    void getMyProfile_WithAuthenticatedUser_ReturnsUserProfile() throws Exception {
        UserProfileResponse profile = new UserProfileResponse();
        profile.setId(1L);
        profile.setUsername("testuser");
        profile.setEmail("test@example.com");
        profile.setFirstName("Test");
        profile.setLastName("User");

        when(authService.getUserProfile(anyLong())).thenReturn(profile);

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"));
    }

    /*@Test
    void registerStaff_WithAdminRole_ReturnsSuccess() throws Exception {
        when(authService.registerStaff(any(StaffRegistrationRequest.class)))
                .thenReturn(ResponseEntity.ok(new MessageResponse("Staff registered successfully")));

        mockMvc.perform(post("/api/admin/register-staff")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(staffRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Staff registered successfully"));
    } */

   /* @Test
    void signup_WithValidData_ReturnsSuccess() throws Exception {
        when(authService.register(any(SignupRequest.class)))
                .thenReturn(ResponseEntity.ok(new MessageResponse("User registered successfully!")));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }*/

    /*@Test
    void signup_WithExistingUsername_ReturnsBadRequest() throws Exception {
        when(authService.register(any(SignupRequest.class)))
                .thenReturn(ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: Username is already taken!")));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Username is already taken!"));
    }*/
}