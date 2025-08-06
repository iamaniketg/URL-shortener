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

public class UserServiceTest {
    @Mock
    private Connection conn;
    @Mock
    private PreparedStatement stmt;
    @Mock
    private ResultSet rs;

    private UserService userService;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(conn);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
    }

    @Test
    void testRegister() throws SQLException {
        when(rs.next()).thenReturn(false); // Username does not exist
        when(stmt.executeUpdate()).thenReturn(1);

        userService.register("testuser", "password");
        verify(stmt, times(1)).executeUpdate();
    }

    @Test
    void testRegisterUsernameExists() throws SQLException {
        when(rs.next()).thenReturn(true); // Username exists

        assertThrows(IllegalArgumentException.class, () ->
                userService.register("testuser", "password")
        );
    }

    @Test
    void testLoginSuccess() throws SQLException {
        when(rs.next()).thenReturn(true);
        when(rs.getString("password")).thenReturn(Integer.toHexString("password".hashCode()));

        String token = userService.login("testuser", "password");
        assertNotNull(token);
        verify(stmt, times(1)).executeQuery();
    }

    @Test
    void testLoginInvalidCredentials() throws SQLException {
        when(rs.next()).thenReturn(true);
        when(rs.getString("password")).thenReturn(Integer.toHexString("wrong".hashCode()));

        assertThrows(IllegalArgumentException.class, () ->
                userService.login("testuser", "password")
        );
    }
}