package com.kadirkara.auth.dto;

public class AuthResponse {
    private String token;

    public AuthResponse() {
        // Default constructor
    }
    public AuthResponse(String token) {
        this.setToken(token);
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
