package com.group2.backend.controllers;

import com.group2.backend.payloads.PaymentRequest;
import com.group2.backend.payloads.PaymentResponse;
import com.group2.backend.service.StripeService;
import com.group2.backend.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.backend.payloads.ConfirmPaymentRequest;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StripeService stripeService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PaymentController paymentController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String TEST_PAYMENT_INTENT = "pi_test123";
    private final String TEST_PAYMENT_METHOD = "pm_test123";

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");
        
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void checkout_ShouldReturnPaymentView() throws Exception {
        mockMvc.perform(get("/payments/checkout"))
               .andExpect(status().isOk())
               .andExpect(view().name("payment"))
               .andExpect(model().attributeExists("stripePublicKey"));
    }

    @Test
    void createPaymentIntent_ShouldReturnSuccess_WhenValidRequest() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(new BigDecimal("10.00"));
        request.setCurrency("usd");
        request.setDescription("Test payment");
        request.setCustomerEmail("test@example.com");

        PaymentResponse response = new PaymentResponse("success", "Payment intent created");
        response.setClientSecret("test_client_secret");

        when(stripeService.createPaymentIntent(any(PaymentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/payments/create-payment-intent")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.status").value("success"))
               .andExpect(jsonPath("$.clientSecret").exists());
    }

    @Test
    void success_ShouldReturnSuccessView_WithPaymentIntent() throws Exception {
        mockMvc.perform(get("/payments/success")
               .param("payment_intent", TEST_PAYMENT_INTENT))
               .andExpect(status().isOk())
               .andExpect(view().name("success"))
               .andExpect(model().attribute("paymentIntentId", TEST_PAYMENT_INTENT));
    }

    @Test
    void checkPaymentStatus_ShouldReturnStatus_WhenValidPaymentIntent() throws Exception {
        PaymentResponse response = new PaymentResponse("succeeded", "Payment succeeded");
        when(stripeService.checkPaymentStatus(TEST_PAYMENT_INTENT)).thenReturn(response);

        mockMvc.perform(get("/payments/check-payment-status/{paymentIntentId}", TEST_PAYMENT_INTENT))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.status").value("succeeded"));
    }

    @Test
    void confirmPayment_ShouldReturnSuccess_WhenValidRequest() throws Exception {
        ConfirmPaymentRequest request = new ConfirmPaymentRequest();
        request.setPaymentIntentId(TEST_PAYMENT_INTENT);
        request.setPaymentMethodId(TEST_PAYMENT_METHOD);
        request.setCustomerEmail("test@example.com");
        request.setCustomerName("Test User");

        PaymentResponse response = new PaymentResponse("succeeded", "Payment confirmed");
        response.setAmount(1000L);
        when(stripeService.confirmPayment(anyString(), anyString())).thenReturn(response);

        mockMvc.perform(post("/payments/confirm-payment")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.status").value("succeeded"));
    }

    @Test
    void confirmPayment_ShouldReturnBadRequest_WhenMissingPaymentIntentId() throws Exception {
        ConfirmPaymentRequest request = new ConfirmPaymentRequest();
        request.setPaymentMethodId(TEST_PAYMENT_METHOD);

        mockMvc.perform(post("/payments/confirm-payment")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest());
    }
}
