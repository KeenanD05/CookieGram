package com.group2.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        // --- START OF FIX ---
        // Load .env if it exists, but don't crash if it's missing (Docker handles vars)
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        // Load system properties from the environment
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });
        // --- END OF FIX ---

        SpringApplication.run(BackendApplication.class, args);
    }
}