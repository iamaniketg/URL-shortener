package com.urlshortener.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.urlshortener.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

public class LoginHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(LoginHandler.class);
    private final UserService userService;

    public LoginHandler(Connection conn) {
        this.userService = new UserService(conn);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"message\": \"Method not allowed\"}");
            return;
        }

        try {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String[] parts = requestBody.replaceAll("[{}\"]", "").split(",");
            String username = null, password = null;
            for (String part : parts) {
                if (part.startsWith("username:")) {
                    username = part.split(":")[1];
                } else if (part.startsWith("password:")) {
                    password = part.split(":")[1];
                }
            }
            if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
                sendResponse(exchange, 400, "{\"message\": \"Invalid input\"}");
                return;
            }
            String token = userService.login(username, password);
            sendResponse(exchange, 200, "{\"token\": \"" + token + "\"}");
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 401, "{\"message\": \"" + e.getMessage() + "\"}");
        } catch (SQLException e) {
            logger.error("Database error", e);
            sendResponse(exchange, 500, "{\"message\": \"Server error\"}");
        }
    }

    private void sendResponse(HttpExchange exchange, int status, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}