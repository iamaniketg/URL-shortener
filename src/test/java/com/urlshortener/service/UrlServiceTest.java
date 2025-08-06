package com.urlshortener.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UrlServiceTest {
    @Mock
    private Connection conn;
    @Mock
    private PreparedStatement stmt;
    @Mock
    private ResultSet rs;

    private UrlService urlService;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        urlService = new UrlService(conn);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
    }

    @Test
    void testShortenUrl() throws SQLException {
        when(rs.next()).thenReturn(false); // Short URL does not exist
        when(stmt.executeUpdate()).thenReturn(1);

        String shortUrl = urlService.shortenUrl("http://example.com", null);
        assertNotNull(shortUrl);
        assertEquals(6, shortUrl.length());
        verify(stmt, times(1)).executeUpdate();
    }

    @Test
    void testCreateCustomUrl() throws SQLException {
        when(rs.next()).thenReturn(false); // Custom URL does not exist
        when(stmt.executeUpdate()).thenReturn(1);

        String shortUrl = urlService.createCustomUrl("custom", "http://example.com", "user");
        assertEquals("custom", shortUrl);
        verify(stmt, times(1)).executeUpdate();
    }

    @Test
    void testCreateCustomUrlAlreadyExists() throws SQLException {
        when(rs.next()).thenReturn(true); // Custom URL exists

        assertThrows(IllegalArgumentException.class, () ->
                urlService.createCustomUrl("custom", "http://example.com", "user")
        );
    }

    @Test
    void testGetLongUrl() throws SQLException {
        when(rs.next()).thenReturn(true);
        when(rs.getString("long_url")).thenReturn("http://example.com");

        String longUrl = urlService.getLongUrl("short");
        assertEquals("http://example.com", longUrl);
        verify(stmt, times(1)).executeQuery();
    }
}