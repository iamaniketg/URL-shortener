package com.urlshortener.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.urlshortener.service.UrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;

public class RedirectHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(RedirectHandler.class);
    private final UrlService urlService;

    public RedirectHandler(Connection conn) {
        this.urlService = new UrlService(conn);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath().substring(1);
        if (path.isEmpty()) {
            // Serve index.html for root path
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("static/index.html")) {
                if (is == null) {
                    sendResponse(exchange, 404, "Not found");
                    return;
                }
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    is.transferTo(os);
                }
            }
            return;
        }

        try {
            String longUrl = urlService.getLongUrl(path);
            if (longUrl != null) {
                exchange.getResponseHeaders().set("Location", longUrl);
                exchange.sendResponseHeaders(302, -1);
                logger.info("Redirected {} to {}", path, longUrl);
            } else {
                sendResponse(exchange, 404, "URL not found");
            }
        } catch (SQLException e) {
            logger.error("Database error", e);
            sendResponse(exchange, 500, "Server error");
        }
    }

    private void sendResponse(HttpExchange exchange, int status, String response) throws IOException {
        exchange.sendResponseHeaders(status, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}