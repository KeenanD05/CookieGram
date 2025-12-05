package com.group2.backend.controllers;

import com.group2.backend.payloads.PaymentRequest;
import com.group2.backend.payloads.PaymentResponse;
import com.group2.backend.service.EmailService;
import com.group2.backend.service.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.group2.backend.payloads.ConfirmPaymentRequest;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final StripeService stripeService;
    private final EmailService emailService;

    @Autowired
    public PaymentController(StripeService stripeService, EmailService emailService) {
        this.stripeService = stripeService;
        this.emailService = emailService;
    }

    @Value("${stripe.public.key}")
    private String stripePublicKey;

    @GetMapping("/checkout")
    public ModelAndView checkout() {
        ModelAndView modelAndView = new ModelAndView("payment");
        modelAndView.addObject("stripePublicKey", stripePublicKey);
        return modelAndView;
    }

    @PostMapping("/create-payment-intent")
    @ResponseBody
    public ResponseEntity<PaymentResponse> createPaymentIntent(@RequestBody PaymentRequest paymentRequest) {
        try {
            PaymentResponse response = stripeService.createPaymentIntent(paymentRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new PaymentResponse("error", e.getMessage()));
        }
    }
    
    @GetMapping("/success")
    public ModelAndView success(@RequestParam("payment_intent") String paymentIntentId) {
        ModelAndView modelAndView = new ModelAndView("success");
        modelAndView.addObject("paymentIntentId", paymentIntentId);
        return modelAndView;
    }


    @GetMapping("/check-payment-status/{paymentIntentId}")
    public ResponseEntity<PaymentResponse> checkPaymentStatus(@PathVariable String paymentIntentId) {
        try {
            PaymentResponse response = stripeService.checkPaymentStatus(paymentIntentId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new PaymentResponse("error", e.getMessage()));
        }
    }

    @PostMapping("/confirm-payment")
    @ResponseBody
    public ResponseEntity<?> confirmPayment(@RequestBody ConfirmPaymentRequest request) {
        try {
            if (request.getPaymentIntentId() == null || request.getPaymentIntentId().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Payment Intent ID is required");
            }

            PaymentResponse response = stripeService.confirmPayment(
                    request.getPaymentIntentId(),
                    request.getPaymentMethodId()
            );

            if ("succeeded".equals(response.getStatus())) {
                // Send confirmation email on successful payment
                try {
                    String customerEmail = request.getCustomerEmail();
                    String customerName = request.getCustomerName();
                    String amount = response.getAmount() != null ? 
                                  String.format("$%,.2f", response.getAmount() / 100.0) : 
                                  "$0.00";
                    
                    if (customerEmail != null && !customerEmail.isEmpty()) {
                        emailService.sendPaymentConfirmationEmail(
                            customerEmail,
                            customerName != null ? customerName : "Valued Customer",
                            amount,
                            request.getPaymentIntentId()
                        );
                    }
                } catch (Exception e) {
                    System.err.println("Failed to send confirmation email: " + e.getMessage());
                }
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(response.getMessage() != null ? response.getMessage() : "Payment failed");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage() != null ? e.getMessage() : "Error processing payment");
        }
    }

    @PostMapping("/cancel-payment/{paymentIntentId}")
    public ResponseEntity<PaymentResponse> cancelPayment(@PathVariable String paymentIntentId) {
        try {
            PaymentResponse response = stripeService.cancelPayment(paymentIntentId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new PaymentResponse("error", e.getMessage()));
        }
    }

    @GetMapping("/test-email")
    public ResponseEntity<String> testEmail(@RequestParam(required = false, defaultValue = "test@example.com") String email) {
        try {
            emailService.sendPaymentConfirmationEmail(
                email,
                "Test User",
                "$49.99",
                "TEST-123"
            );
            return ResponseEntity.ok("Test email sent successfully to " + email);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to send test email: " + e.getMessage());
        }
    }
}
