package com.group2.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
// ---------------------------------------------------------------------------
// FIX: Force H2 Database and Mock Secrets for CI Environment
// ---------------------------------------------------------------------------
@TestPropertySource(properties = {
		// 1. Database Config (H2 In-Memory)
		"spring.datasource.url=jdbc:h2:mem:testdb",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=password",
		"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
		"spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",

		// 2. Email Config (Mock values to prevent startup error)
		"EMAIL_APP_USER=test@example.com",
		"EMAIL_APP_PASSWORD=testpassword",

		// 3. Stripe Config (Mock values to prevent startup error)
		"STRIPE_API_KEY=sk_test_dummy_key",
		"STRIPE_PUBLIC_KEY=pk_test_dummy_key",
		"STRIPE_WEBHOOK_SECRET=whsec_dummy_secret"
})
class BackendApplicationTests {

	@Test
	void contextLoads() {
		// This test ensures the Spring Context loads successfully.
		// If the configuration above is correct, this will pass.
	}

}