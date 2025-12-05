package com.group2.backend.controllers;

import com.group2.backend.config.PaginationConfig;
import com.group2.backend.exception.ResourceNotFoundException;
import com.group2.backend.payloads.OrderResponse;
import com.group2.backend.repository.UserRepository;
import com.group2.backend.security.request.StaffRegistrationRequest;
import com.group2.backend.security.response.StaffResponse;
import com.group2.backend.service.AuthService;
import com.group2.backend.service.OrderService;
import com.group2.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private PaginationConfig paginationConfig;

    @Autowired
    private OrderService orderService;


    @PostMapping("/register-staff")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerStaff(
            @Valid @RequestBody StaffRegistrationRequest staffRequest) {
        return authService.registerStaff(staffRequest);
    }

    @DeleteMapping("/delete-staff/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteStaff(@PathVariable Long id) {
        try{
            userService.deleteStaff(id);
            return ResponseEntity.ok().build();
        }catch(ResourceNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get-staff/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getStaff(@PathVariable Long id) {

        try{
            return ResponseEntity.ok(userService.getStaff(id));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get-all-staff")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<StaffResponse>> getAllStaff(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction
    ) {
        int pageNumber = (page != null) ? page : paginationConfig.getDefaultPage();
        int pageSize = (size != null) ? size : paginationConfig.getDefaultSize();
        String sortBy = (sort != null) ? sort : paginationConfig.getDefaultSortBy();
        String sortDirection = (direction != null) ? direction : paginationConfig.getDefaultSortDirection();
        return ResponseEntity.ok(userService.getAllStaff(pageNumber, pageSize, sortBy, sortDirection));
    }



}
