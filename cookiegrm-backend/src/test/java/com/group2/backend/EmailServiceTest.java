package com.group2.backend;

import com.group2.backend.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Test
    public void testSendEmail() {
        try {
            // Test sending a payment confirmation email
            emailService.sendPaymentConfirmationEmail(
                "test@example.com",  // This will be captured by Mailtrap
                "Test User",
                "$49.99",
                "TEST-123"
            );
            System.out.println("Test email sent successfully!");
        } catch (Exception e) {
            System.err.println("Failed to send test email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
