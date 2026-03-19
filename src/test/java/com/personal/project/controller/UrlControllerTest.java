package com.personal.project.controller;

import com.personal.project.service.UrlShortenerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlShortenerService urlShortenerService;

    @Test
    void shorten_validUrl_returns200WithShortCode() throws Exception {
        when(urlShortenerService.shortenUrl("https://example.com")).thenReturn("abc123");

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").value("abc123"))
                .andExpect(jsonPath("$.originalUrl").value("https://example.com"))
                .andExpect(jsonPath("$.shortUrl").isNotEmpty());
    }

    @Test
    void shorten_missingUrl_returns400() throws Exception {
        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shorten_invalidUrlScheme_returns400() throws Exception {
        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"ftp://example.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void redirect_knownCode_returns302() throws Exception {
        when(urlShortenerService.getOriginalUrl("abc123")).thenReturn(Optional.of("https://example.com"));

        mockMvc.perform(get("/abc123"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com"));
    }

    @Test
    void redirect_unknownCode_returns404() throws Exception {
        when(urlShortenerService.getOriginalUrl(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/xxxxxx"))
                .andExpect(status().isNotFound());
    }

    @Test
    void stats_knownCode_returns200() throws Exception {
        com.personal.project.model.UrlMapping mapping =
                new com.personal.project.model.UrlMapping("abc123", "https://example.com");
        when(urlShortenerService.getMapping("abc123")).thenReturn(Optional.of(mapping));

        mockMvc.perform(get("/stats/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").value("abc123"))
                .andExpect(jsonPath("$.originalUrl").value("https://example.com"))
                .andExpect(jsonPath("$.clickCount").value(0));
    }

    @Test
    void stats_unknownCode_returns404() throws Exception {
        when(urlShortenerService.getMapping(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/stats/xxxxxx"))
                .andExpect(status().isNotFound());
    }
}
