package com.group2.backend.service;

import com.group2.backend.model.AppRole;
import com.group2.backend.model.Role;
import com.group2.backend.model.User;
import com.group2.backend.payloads.AuthenticationResult;
import com.group2.backend.payloads.UserProfileResponse;
import com.group2.backend.repository.RoleRepository;
import com.group2.backend.repository.UserRepository;
import com.group2.backend.security.jwt.JwtUtils;
import com.group2.backend.security.request.LoginRequest;
import com.group2.backend.security.request.SignupRequest;
import com.group2.backend.security.request.StaffRegistrationRequest;
import com.group2.backend.security.response.JwtResponse;
import com.group2.backend.security.response.MessageResponse;
import com.group2.backend.security.response.StaffResponse;
import com.group2.backend.security.response.UserResponse;
import com.group2.backend.security.service.UserDetailsImpl;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService{

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RoleRepository roleRepository;


    @Override
    public ResponseEntity<AuthenticationResult> login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        // Fetch the full user object to get additional profile information
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userDetails.getId()));

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        JwtResponse jwtResponse = new JwtResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roles);

        return ResponseEntity.ok(new AuthenticationResult(jwtResponse, jwtCookie));
    }

    @Override
    public ResponseCookie logout() {
        return jwtUtils.getCleanJwtCookie();
    }

    @Transactional
    @Override
    public ResponseEntity<?> register(SignupRequest signUpRequest) {
        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                signUpRequest.getMobileNumber()
        );

        // Set default role to USER
        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        user.setRoles(new HashSet<>());
        user.getRoles().add(userRole);

        User savedUser = userRepository.save(user);

        // Create and return response
        UserResponse response = new UserResponse(
                savedUser.getId(),
                savedUser.getUserName(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getRoles().stream()
                        .map(role -> role.getRoleName().name())
                        .collect(Collectors.toList())
        );

        return ResponseEntity.ok(response);
    }

    // Add this method to your AuthServiceImpl class
    @Transactional
    @Override
    public ResponseEntity<?> registerStaff(StaffRegistrationRequest staffRequest) {
        // Check if username is already taken
        if (userRepository.existsByUserName(staffRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        // Check if email is already in use
        if (userRepository.existsByEmail(staffRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new staff user
        User user = new User(
                staffRequest.getUsername(),
                staffRequest.getEmail(),
                passwordEncoder.encode(staffRequest.getPassword()),
                staffRequest.getFirstName(),
                staffRequest.getLastName(),
                staffRequest.getMobileNumber()
        );

        // Set role to STAFF
        Role staffRole = roleRepository.findByRoleName(AppRole.ROLE_STAFF)
                .orElseThrow(() -> new RuntimeException("Error: Staff role not found."));
        user.setRoles(new HashSet<>());
        user.getRoles().add(staffRole);

        User savedUser = userRepository.save(user);

        // Create and return response
        StaffResponse response = new StaffResponse(
                savedUser.getId(),
                savedUser.getUserName(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getMobileNumber()
        );

        return ResponseEntity.ok(response);
    }

    // Add this method to your AuthServiceImpl class
    @Override
    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return new UserProfileResponse(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getMobileNumber(),
                user.getRoles().stream()
                        .map(role -> role.getRoleName().name())
                        .collect(Collectors.toList()),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
