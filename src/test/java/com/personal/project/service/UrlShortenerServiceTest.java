package com.personal.project.service;

import com.personal.project.model.UrlMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UrlShortenerServiceTest {

    private UrlShortenerService service;

    @BeforeEach
    void setUp() {
        service = new UrlShortenerService();
    }

    @Test
    void shortenUrl_returnsNonEmptyCode() {
        String code = service.shortenUrl("https://example.com");
        assertThat(code).isNotBlank();
    }

    @Test
    void shortenUrl_sameUrlReturnsSameCode() {
        String code1 = service.shortenUrl("https://example.com");
        String code2 = service.shortenUrl("https://example.com");
        assertThat(code1).isEqualTo(code2);
    }

    @Test
    void shortenUrl_differentUrlsReturnDifferentCodes() {
        String code1 = service.shortenUrl("https://example.com/a");
        String code2 = service.shortenUrl("https://example.com/b");
        assertThat(code1).isNotEqualTo(code2);
    }

    @Test
    void getOriginalUrl_returnsUrlForValidCode() {
        String code = service.shortenUrl("https://example.com");
        Optional<String> result = service.getOriginalUrl(code);
        assertThat(result).isPresent().contains("https://example.com");
    }

    @Test
    void getOriginalUrl_incrementsClickCount() {
        String code = service.shortenUrl("https://example.com");
        service.getOriginalUrl(code);
        service.getOriginalUrl(code);
        Optional<UrlMapping> mapping = service.getMapping(code);
        assertThat(mapping).isPresent();
        assertThat(mapping.get().getClickCount()).isEqualTo(2);
    }

    @Test
    void getOriginalUrl_returnsEmptyForUnknownCode() {
        Optional<String> result = service.getOriginalUrl("xxxxxx");
        assertThat(result).isEmpty();
    }

    @Test
    void getMapping_returnsEmptyForUnknownCode() {
        Optional<UrlMapping> result = service.getMapping("xxxxxx");
        assertThat(result).isEmpty();
    }

    @Test
    void shortCodeIsExactlySixCharacters() {
        String code = service.shortenUrl("https://example.com");
        assertThat(code).hasSize(6);
    }
}
