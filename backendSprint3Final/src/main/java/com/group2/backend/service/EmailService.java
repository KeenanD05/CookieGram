package com.group2.backend.service;

public interface EmailService {
    void sendPaymentConfirmationEmail(String to, String customerName, String paymentAmount, String paymentId);
    void sendSimpleMessage(String to, String subject, String text);
}
