package com.personal.project.service;

import com.personal.project.model.UrlMapping;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UrlShortenerService {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;

    private final SecureRandom random = new SecureRandom();
    private final Map<String, UrlMapping> shortToLong = new ConcurrentHashMap<>();
    private final Map<String, String> longToShort = new ConcurrentHashMap<>();

    /**
     * Shortens the given URL. If the URL has already been shortened, returns
     * the existing short code.
     *
     * @param originalUrl the URL to shorten
     * @return the generated (or existing) short code
     */
    public String shortenUrl(String originalUrl) {
        if (longToShort.containsKey(originalUrl)) {
            return longToShort.get(originalUrl);
        }

        String code;
        do {
            code = generateCode();
        } while (shortToLong.containsKey(code));

        UrlMapping mapping = new UrlMapping(code, originalUrl);
        shortToLong.put(code, mapping);
        longToShort.put(originalUrl, code);
        return code;
    }

    /**
     * Returns the original URL for the given short code, incrementing its
     * click counter.
     *
     * @param code the short code
     * @return an {@link Optional} containing the original URL, or empty if not found
     */
    public Optional<String> getOriginalUrl(String code) {
        UrlMapping mapping = shortToLong.get(code);
        if (mapping == null) {
            return Optional.empty();
        }
        mapping.incrementClickCount();
        return Optional.of(mapping.getOriginalUrl());
    }

    /**
     * Returns the {@link UrlMapping} for the given short code without
     * incrementing the click counter.
     *
     * @param code the short code
     * @return an {@link Optional} containing the mapping, or empty if not found
     */
    public Optional<UrlMapping> getMapping(String code) {
        return Optional.ofNullable(shortToLong.get(code));
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
