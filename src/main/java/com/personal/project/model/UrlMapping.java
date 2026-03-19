package com.personal.project.model;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

public class UrlMapping {

    private final String shortCode;
    private final String originalUrl;
    private final LocalDateTime createdAt;
    private final AtomicLong clickCount;

    public UrlMapping(String shortCode, String originalUrl) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.createdAt = LocalDateTime.now();
        this.clickCount = new AtomicLong(0);
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public long getClickCount() {
        return clickCount.get();
    }

    public void incrementClickCount() {
        this.clickCount.incrementAndGet();
    }
}
