package com.urlshortener.service;

import com.urlshortener.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final Connection conn;

    public UserService(Connection conn) {
        this.conn = conn;
    }

    public void register(String username, String password) throws SQLException {
        if (isUserExists(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password));
            stmt.executeUpdate();
            logger.info("Registered user: {}", username);
        }
    }

    public String login(String username, String password) throws SQLException {
        String query = "SELECT password FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getString("password").equals(hashPassword(password))) {
                String token = UUID.randomUUID().toString();
                logger.info("User logged in: {}", username);
                return token;
            }
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    private boolean isUserExists(String username) throws SQLException {
        String query = "SELECT 1 FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            return stmt.executeQuery().next();
        }
    }

    private String hashPassword(String password) {
        // Simple hash for demo purposes (in production, use BCrypt or similar)
        return Integer.toHexString(password.hashCode());
    }
}