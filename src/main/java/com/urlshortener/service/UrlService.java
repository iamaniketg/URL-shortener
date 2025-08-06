package com.urlshortener.service;

import com.urlshortener.model.Url;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class UrlService {
    private static final Logger logger = LoggerFactory.getLogger(UrlService.class);
    private final Connection conn;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_URL_LENGTH = 6;

    public UrlService(Connection conn) {
        this.conn = conn;
    }

    public String shortenUrl(String longUrl, String username) throws SQLException {
        String shortUrl = generateShortUrl();
        while (isShortUrlExists(shortUrl)) {
            shortUrl = generateShortUrl();
        }
        saveUrl(shortUrl, longUrl, username);
        logger.info("Shortened URL: {} to {}", longUrl, shortUrl);
        return shortUrl;
    }

    public String createCustomUrl(String customUrl, String longUrl, String username) throws SQLException {
        if (isShortUrlExists(customUrl)) {
            throw new IllegalArgumentException("Custom URL already exists");
        }
        saveUrl(customUrl, longUrl, username);
        logger.info("Created custom URL: {} to {} for user {}", customUrl, longUrl, username);
        return customUrl;
    }

    public String getLongUrl(String shortUrl) throws SQLException {
        String query = "SELECT long_url FROM urls WHERE short_url = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, shortUrl);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("long_url");
            }
            return null;
        }
    }

    private String generateShortUrl() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SHORT_URL_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    private boolean isShortUrlExists(String shortUrl) throws SQLException {
        String query = "SELECT 1 FROM urls WHERE short_url = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, shortUrl);
            return stmt.executeQuery().next();
        }
    }

    private void saveUrl(String shortUrl, String longUrl, String username) throws SQLException {
        String query = "INSERT INTO urls (short_url, long_url, username) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, shortUrl);
            stmt.setString(2, longUrl);
            stmt.setString(3, username);
            stmt.executeUpdate();
        }
    }
}