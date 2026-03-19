# URL Shortener — TinyURL-like Service

A lightweight URL shortening service built with **Spring Boot 3** and Java 17.

## Features

| Feature | Details |
|---|---|
| Shorten URL | `POST /shorten` — generate a 6-character alphanumeric short code |
| Redirect | `GET /{code}` — HTTP 302 redirect to the original URL |
| Stats | `GET /stats/{code}` — view click count and metadata |
| Deduplication | Same URL always returns the same short code |
| Thread-safe | `ConcurrentHashMap` storage — safe under concurrent load |

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+

### Run

```bash
mvn spring-boot:run
```

The server starts on **http://localhost:8080**.

### Shorten a URL

```bash
curl -s -X POST http://localhost:8080/shorten \
  -H "Content-Type: application/json" \
  -d '{"url":"https://www.example.com/very/long/path?query=value"}'
```

Response:

```json
{
  "shortCode": "aB3xYz",
  "shortUrl": "http://localhost:8080/aB3xYz",
  "originalUrl": "https://www.example.com/very/long/path?query=value"
}
```

### Use the Short URL

Open **http://localhost:8080/aB3xYz** in a browser — you will be redirected automatically.

Or with curl:

```bash
curl -L http://localhost:8080/aB3xYz
```

### View Statistics

```bash
curl http://localhost:8080/stats/aB3xYz
```

Response:

```json
{
  "shortCode": "aB3xYz",
  "originalUrl": "https://www.example.com/very/long/path?query=value",
  "createdAt": "2026-03-19T16:00:00",
  "clickCount": 3
}
```

## API Reference

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/shorten` | Shorten a URL. Body: `{"url": "<original-url>"}` |
| `GET` | `/{code}` | Redirect to the original URL |
| `GET` | `/stats/{code}` | Return metadata and click count |

### Error responses

- **400 Bad Request** — missing or invalid `url` field (must start with `http://` or `https://`)
- **404 Not Found** — unknown short code

## Running Tests

```bash
mvn test
```

## Project Structure

```
src/
 └── main/java/com/personal/project/
      ├── UrlShortenerApplication.java   # Spring Boot entry point
      ├── controller/UrlController.java  # REST endpoints
      ├── service/UrlShortenerService.java # Core shortening logic
      └── model/UrlMapping.java          # URL mapping entity
 └── test/java/com/personal/project/
      ├── controller/UrlControllerTest.java
      └── service/UrlShortenerServiceTest.java
```
