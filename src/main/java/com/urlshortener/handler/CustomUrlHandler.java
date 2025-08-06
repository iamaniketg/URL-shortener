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

public class CustomUrlHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomUrlHandler.class);
    private final UrlService urlService;

    public CustomUrlHandler(Connection conn) {
        this.urlService = new UrlService(conn);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"message\": \"Method not allowed\"}");
            return;
        }

        String auth = exchange.getRequestHeaders().getFirst("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            sendResponse(exchange, 401, "{\"message\": \"Unauthorized\"}");
            return;
        }

        try {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String[] parts = requestBody.replaceAll("[{}\"]", "").split(",");
            String longUrl = null, customUrl = null;
            for (String part : parts) {
                if (part.startsWith("longUrl:")) {
                    longUrl = part.split(":")[1];
                } else if (part.startsWith("customUrl:")) {
                    customUrl = part.split(":")[1];
                }
            }
            if (!isValidUrl(longUrl) || customUrl == null || customUrl.isEmpty()) {
                sendResponse(exchange, 400, "{\"message\": \"Invalid input\"}");
                return;
            }
            String shortUrl = urlService.createCustomUrl(customUrl, longUrl, "user"); // Simplified user
            sendResponse(exchange, 200, "{\"shortUrl\": \"" + shortUrl + "\"}");
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, "{\"message\": \"" + e.getMessage() + "\"}");
        } catch (SQLException e) {
            logger.error("Database error", e);
            sendResponse(exchange, 500, "{\"message\": \"Server error\"}");
        }
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