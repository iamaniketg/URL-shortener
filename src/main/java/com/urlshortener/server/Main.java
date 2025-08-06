package com.urlshortener.server;

import com.sun.net.httpserver.HttpServer;
import com.urlshortener.handler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            // Initialize H2 Database
            Connection conn = DriverManager.getConnection("jdbc:h2:./urlshortener;DB_CLOSE_DELAY=-1", "sa", "");
            initDatabase(conn);

            // Create HTTP Server
            HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
            server.createContext("/api/shorten", new ShortenHandler(conn));
            server.createContext("/api/custom", new CustomUrlHandler(conn));
            server.createContext("/api/register", new RegisterHandler(conn));
            server.createContext("/api/login", new LoginHandler(conn));
            server.createContext("/static/", new StaticFileHandler());
            server.createContext("/", new RedirectHandler(conn));
            server.setExecutor(null);
            server.start();
            logger.info("Server started on port 8000");
        } catch (Exception e) {
            logger.error("Server startup failed", e);
        }
    }

    private static void initDatabase(Connection conn) throws SQLException {
        conn.createStatement().execute("CREATE TABLE IF NOT EXISTS users (username VARCHAR(255) PRIMARY KEY, password VARCHAR(255))");
        conn.createStatement().execute("CREATE TABLE IF NOT EXISTS urls (short_url VARCHAR(255) PRIMARY KEY, long_url VARCHAR(255), username VARCHAR(255))");
        logger.info("Database initialized");
    }
}