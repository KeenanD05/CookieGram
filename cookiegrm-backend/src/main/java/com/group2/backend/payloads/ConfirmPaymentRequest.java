package com.group2.backend.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfirmPaymentRequest {
    @NotBlank(message = "Payment Intent ID is required")
    private String paymentIntentId;
    
    private String paymentMethodId;  // This is optional
    
    @NotBlank(message = "Customer email is required")
    @Email(message = "Email should be valid")
    private String customerEmail;
    
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    // Optional: If you want to override the amount from the payment intent
    private String amount;
}
