package com.group2.backend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.group2.backend.controllers.CookieController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
// ---------------------------------------------------------------------------
// FIX: Override Database, Email, AND Stripe variables for testing
// ---------------------------------------------------------------------------
@TestPropertySource(properties = {
        // 1. Database Config (Use H2 In-Memory)
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=password",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",

        // 2. Email Config (Mock values)
        "EMAIL_APP_USER=test@example.com",
        "EMAIL_APP_PASSWORD=testpassword",

        // 3. Stripe Config (Mock values - New addition to fix your error)
        "STRIPE_API_KEY=sk_test_dummy_key_12345",
        "STRIPE_PUBLIC_KEY=pk_test_dummy_key_12345",
        "STRIPE_WEBHOOK_SECRET=whsec_dummy_secret_12345"
})
class SmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CookieController cookieController;

    /**
     * Smoke Test 1: App Context Check
     * Verifies that the Spring application actually starts up.
     */
    @Test
    void contextLoads() {
        assertThat(cookieController).isNotNull();
    }

    /**
     * Smoke Test 2: Health Check
     * Verifies that the container is reachable.
     */
    @Test
    void healthEndpointReturnsOk() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk());
    }

    /**
     * Smoke Test 3: Public API Check
     * Verifies that the public API is responding.
     */
    @Test
    void publicCookiesEndpointReturnsOk() throws Exception {
        mockMvc.perform(get("/api/public/cookies"))
                .andExpect(status().isOk());
    }
}