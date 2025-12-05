package com.group2.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "pagination")
public class PaginationConfig {
    private int defaultPage = 0;
    private int defaultSize = 10;
    private String defaultSortBy = "id";
    private String defaultSortDirection = "asc";

    // Getters and Setters
    public int getDefaultPage() {
        return defaultPage;
    }

    public void setDefaultPage(int defaultPage) {
        this.defaultPage = defaultPage;
    }

    public int getDefaultSize() {
        return defaultSize;
    }

    public void setDefaultSize(int defaultSize) {
        this.defaultSize = defaultSize;
    }

    public String getDefaultSortBy() {
        return defaultSortBy;
    }

    public void setDefaultSortBy(String defaultSortBy) {
        this.defaultSortBy = defaultSortBy;
    }

    public String getDefaultSortDirection() {
        return defaultSortDirection;
    }

    public void setDefaultSortDirection(String defaultSortDirection) {
        this.defaultSortDirection = defaultSortDirection;
    }
}
