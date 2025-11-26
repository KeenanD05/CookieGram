package com.group2.backend.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvConfig {
    
    @Bean
    public Dotenv dotenv() {
        // Load .env file from the root directory of the project
        return Dotenv.configure()
                .directory("D:\\Sems\\Sem 5\\Backup\\backend") // Update this path if needed
                .ignoreIfMissing()
                .load();
    }
}
