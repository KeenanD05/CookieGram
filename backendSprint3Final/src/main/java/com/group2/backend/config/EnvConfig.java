package com.group2.backend.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvConfig {

    @Bean
    public Dotenv dotenv() {
        // Updated for Docker: removed hardcoded Windows directory
        return Dotenv.configure()
                .ignoreIfMissing()
                .load();
    }
}