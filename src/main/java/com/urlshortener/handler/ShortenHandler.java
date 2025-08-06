package com.urlshortener.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.urlshortener.service.UrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

public class ShortenHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(ShortenHandler.class);
    private final UrlService urlService;

    public ShortenHandler(Connection conn) {
        this.urlService = new UrlService(conn);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"message\": \"Method not allowed\"}");
            return;
        }

        try {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String longUrl = parseLongUrl(requestBody);
            if (!isValidUrl(longUrl)) {
                sendResponse(exchange, 400, "{\"message\": \"Invalid URL\"}");
                return;
            }
            String shortUrl = urlService.shortenUrl(longUrl, null);
            sendResponse(exchange, 200, "{\"shortUrl\": \"" + shortUrl + "\"}");
        } catch (SQLException e) {
            logger.error("Database error", e);
            sendResponse(exchange, 500, "{\"message\": \"Server error\"}");
        }
    }

    private String parseLongUrl(String requestBody) {
        // Simple JSON parsing for demo
        String[] parts = requestBody.replaceAll("[{}\"]", "").split(",");
        for (String part : parts) {
            if (part.startsWith("longUrl:")) {
                return part.split(":")[1];
            }
        }
        return "";
    }

    private boolean isValidUrl(String url) {
        return url != null && url.matches("https?://.+");
    }

    private void sendResponse(HttpExchange exchange, int status, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}