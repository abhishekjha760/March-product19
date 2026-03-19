package com.personal.project.controller;

import com.personal.project.model.UrlMapping;
import com.personal.project.service.UrlShortenerService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

@RestController
public class UrlController {

    private final UrlShortenerService urlShortenerService;

    public UrlController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    /**
     * Shorten a URL.
     *
     * <p>Request body: {@code {"url": "https://example.com/very/long/path"}}
     * <p>Response: {@code {"shortCode": "abc123", "shortUrl": "http://localhost:8080/abc123"}}
     */
    @PostMapping("/shorten")
    public ResponseEntity<Map<String, String>> shorten(
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-Forwarded-Proto", defaultValue = "http") String proto,
            @RequestHeader(value = "Host", defaultValue = "localhost:8080") String host) {

        String originalUrl = body.get("url");
        if (originalUrl == null || originalUrl.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "The 'url' field is required and must not be blank."));
        }

        if (!originalUrl.startsWith("http://") && !originalUrl.startsWith("https://")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "The URL must start with http:// or https://."));
        }

        String code = urlShortenerService.shortenUrl(originalUrl);
        String shortUrl = proto + "://" + host + "/" + code;

        return ResponseEntity.ok(Map.of(
                "shortCode", code,
                "shortUrl", shortUrl,
                "originalUrl", originalUrl
        ));
    }

    /**
     * Redirect to the original URL using the short code.
     */
    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        Optional<String> originalUrl = urlShortenerService.getOriginalUrl(code);
        if (originalUrl.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(originalUrl.get()));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    /**
     * Get statistics for a short code.
     */
    @GetMapping("/stats/{code}")
    public ResponseEntity<Map<String, Object>> stats(@PathVariable String code) {
        Optional<UrlMapping> mapping = urlShortenerService.getMapping(code);
        if (mapping.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UrlMapping m = mapping.get();
        return ResponseEntity.ok(Map.of(
                "shortCode", m.getShortCode(),
                "originalUrl", m.getOriginalUrl(),
                "createdAt", m.getCreatedAt().toString(),
                "clickCount", m.getClickCount()
        ));
    }
}
