package com.urlshortener.model;

public class Url {
    private String shortUrl;
    private String longUrl;
    private String username;

    public Url(String shortUrl, String longUrl, String username) {
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
        this.username = username;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public String getUsername() {
        return username;
    }
}