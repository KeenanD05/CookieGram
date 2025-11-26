package com.group2.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Locale;

@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${email.sender}")
    private String fromEmail;
    
    @Value("${email.sender.name}")
    private String senderName;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    @Async
    public void sendPaymentConfirmationEmail(String to, String customerName, String paymentAmount, String paymentId) {
        try {
            String subject = "Payment Confirmation - Order #" + paymentId;
            
            // Prepare the email content using Thymeleaf
            Context context = new Context(Locale.getDefault());
            context.setVariable("name", customerName);
            context.setVariable("amount", paymentAmount);
            context.setVariable("paymentId", paymentId);
            
            // Process the Thymeleaf template
            String htmlContent = templateEngine.process("email/payment-confirmation", context);
            
            // Create and send the email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(String.format("%s <%s>", senderName, fromEmail));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            logger.info("Payment confirmation email sent successfully to {}", to);
            
        } catch (MessagingException e) {
            logger.error("Error sending payment confirmation email to " + to, e);
            throw new RuntimeException("Failed to send payment confirmation email", e);
        }
    }

    @Override
    @Async
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(String.format("%s <%s>", senderName, fromEmail));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, false);
            
            mailSender.send(message);
            logger.info("Simple email sent successfully to {}", to);
            
        } catch (MessagingException e) {
            logger.error("Error sending simple email to " + to, e);
            throw new RuntimeException("Failed to send simple email", e);
        }
    }
}
