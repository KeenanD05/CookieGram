package com.group2.backend.controllers;
import com.group2.backend.exception.ResourceNotFoundException;
import com.group2.backend.payloads.CookieDTO;
import com.group2.backend.service.CookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CookieController {

    private final CookieService cookieService;

    @Autowired
    public CookieController(CookieService cookieService) {
        this.cookieService = cookieService;
    }

    @GetMapping("/public/cookies")
    public ResponseEntity<List<CookieDTO>> getAllCookies() {
        return ResponseEntity.ok(cookieService.getAllCookies());
    }

    @GetMapping("/public/cookies/{id}")
    public ResponseEntity<CookieDTO> getCookieById(@PathVariable Long id) {
        try{
            return ResponseEntity.ok(cookieService.getCookieById(id));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/admin/cookies")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CookieDTO> createCookie(@RequestBody CookieDTO cookieDTO) {
        return ResponseEntity.ok(cookieService.createCookie(cookieDTO));
    }

    @PutMapping("/admin/cookies/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CookieDTO> updateCookie(
            @PathVariable Long id,
            @RequestBody CookieDTO cookieDTO) {

        try{
            return ResponseEntity.ok(cookieService.updateCookie(id, cookieDTO));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/admin/cookies/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CookieDTO> deleteCookie(@PathVariable Long id) {
        try{
            CookieDTO cookieDTO= cookieService.getCookieById(id);
            cookieService.deleteCookie(id);
            return ResponseEntity.ok(cookieDTO);
        }catch(ResourceNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }
}
