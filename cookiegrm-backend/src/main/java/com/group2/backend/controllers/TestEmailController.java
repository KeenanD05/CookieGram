package com.group2.backend.controllers;

import com.group2.backend.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestEmailController {

    private final EmailService emailService;

    public TestEmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/send-email")
    public ResponseEntity<String> sendTestEmail(
            @RequestParam String to,
            @RequestParam(required = false, defaultValue = "Test User") String name) {
        
        try {
            // Send a test payment confirmation email
            emailService.sendPaymentConfirmationEmail(
                to,
                name,
                "$49.99",
                "TEST-123"
            );
            
            return ResponseEntity.ok("Test email sent successfully to " + to);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Failed to send test email: " + e.getMessage());
        }
    }
}
