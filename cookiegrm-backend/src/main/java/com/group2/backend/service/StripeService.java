package com.group2.backend.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.group2.backend.payloads.PaymentRequest;
import com.group2.backend.payloads.PaymentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService {

    @Value("${stripe.currency}")
    private String currency;

    @Value("${stripe.api.key}")
    private String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    public PaymentResponse createPaymentIntent(PaymentRequest paymentRequest) throws StripeException {
        try {
            // Convert amount to smallest currency unit (cents)
            long amountInCents = convertToSmallestCurrencyUnit(paymentRequest.getAmount());
            
            // Create payment intent parameters
            PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(currency)
                    .setDescription(paymentRequest.getDescription())
                    .setReceiptEmail(paymentRequest.getCustomerEmail())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods
                                    .builder()
                                    .setEnabled(true)
                                    .build()
                    );

            // Add payment method if provided
            if (paymentRequest.getPaymentMethodId() != null && !paymentRequest.getPaymentMethodId().isEmpty()) {
                paramsBuilder.setPaymentMethod(paymentRequest.getPaymentMethodId());
            }

            // Add success and cancel URLs if provided
            if (paymentRequest.getSuccessUrl() != null) {
                paramsBuilder.setConfirm(true)
                        .setReturnUrl(paymentRequest.getSuccessUrl());
            }

            PaymentIntent paymentIntent = PaymentIntent.create(paramsBuilder.build());

            return new PaymentResponse(
                    paymentIntent.getClientSecret(),
                    paymentIntent.getId(),
                    paymentIntent.getStatus(),
                    paymentIntent.getAmount(),
                    paymentIntent.getCurrency(),
                    paymentRequest.getCustomerEmail()
            );
        } catch (StripeException e) {
            throw new RuntimeException("Error creating payment intent: " + e.getMessage(), e);
        }
    }

    public PaymentResponse checkPaymentStatus(String paymentIntentId) throws StripeException {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            return new PaymentResponse(
                    paymentIntent.getClientSecret(),
                    paymentIntent.getId(),
                    paymentIntent.getStatus(),
                    paymentIntent.getAmount(),
                    paymentIntent.getCurrency(),
                    paymentIntent.getReceiptEmail()
            );
        } catch (StripeException e) {
            throw new RuntimeException("Error checking payment status: " + e.getMessage(), e);
        }
    }

    public PaymentResponse confirmPayment(String paymentIntentId, String paymentMethodId) {
        try {
            // Retrieve the payment intent
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            
            // If already succeeded, return the current status
            if ("succeeded".equals(paymentIntent.getStatus())) {
                return new PaymentResponse(
                    paymentIntent.getClientSecret(),
                    paymentIntent.getId(),
                    paymentIntent.getStatus(),
                    paymentIntent.getAmount(),
                    paymentIntent.getCurrency(),
                    paymentIntent.getReceiptEmail()
                );
            }
            
            // Build confirm parameters
            PaymentIntentConfirmParams params = PaymentIntentConfirmParams.builder()
                .setPaymentMethod(paymentMethodId)
                .setOffSession(true)
                .build();
                
            // Confirm the payment intent with the confirm flag
            PaymentIntent confirmedIntent = paymentIntent.confirm(params);
            
            // Create response with the confirmed intent status
            String status = confirmedIntent.getStatus();
            String message = "succeeded".equals(status) ? "Payment processed successfully" : 
                           "Payment confirmation in status: " + status;
            
            return new PaymentResponse(
                confirmedIntent.getClientSecret(),
                confirmedIntent.getId(),
                status,
                confirmedIntent.getAmount(),
                confirmedIntent.getCurrency(),
                confirmedIntent.getReceiptEmail()
            );
            
        } catch (StripeException e) {
            // Log the error for debugging
            System.err.println("Error confirming payment: " + e.getMessage());
            
            try {
                // Try to get the current state of the payment intent
                PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
                
                // Create response with current status
                PaymentResponse response = new PaymentResponse(
                    paymentIntent.getClientSecret(),
                    paymentIntent.getId(),
                    paymentIntent.getStatus(),
                    paymentIntent.getAmount(),
                    paymentIntent.getCurrency(),
                    paymentIntent.getReceiptEmail()
                );
                response.setMessage("Error processing payment: " + e.getMessage());
                return response;
                
            } catch (StripeException ex) {
                throw new RuntimeException("Error processing payment: " + e.getMessage(), e);
            }
        }
    }

    public PaymentResponse cancelPayment(String paymentIntentId) throws StripeException {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            PaymentIntent canceledPaymentIntent = paymentIntent.cancel();
            return new PaymentResponse(
                    canceledPaymentIntent.getStatus(),
                    "Payment cancelled successfully"
            );
        } catch (StripeException e) {
            throw new RuntimeException("Error canceling payment: " + e.getMessage(), e);
        }
    }

    private long convertToSmallestCurrencyUnit(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100)).longValue();
    }
}
