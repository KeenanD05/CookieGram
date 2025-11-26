package com.group2.backend.payloads;

import lombok.Data;

@Data
public class PaymentResponse {
    private String clientSecret;
    private String paymentIntentId;
    private String status;
    private String message;
    private Long amount;
    private String currency;
    private String customerEmail;

    public PaymentResponse(String clientSecret, String paymentIntentId, String status, Long amount, String currency) {
        this.clientSecret = clientSecret;
        this.paymentIntentId = paymentIntentId;
        this.status = status;
        this.amount = amount;
        this.currency = currency;
        this.message = "Payment processed successfully";
    }

    public PaymentResponse(String clientSecret, String paymentIntentId, String status, Long amount, String currency, String customerEmail) {
        this.clientSecret = clientSecret;
        this.paymentIntentId = paymentIntentId;
        this.status = status;
        this.amount = amount;
        this.currency = currency;
        this.customerEmail = customerEmail;
        this.message = "Payment processed successfully";
    }

    public PaymentResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
