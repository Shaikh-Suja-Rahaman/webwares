package com.example.ecommerce.dto.response.auth;

import java.util.Set;

public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private Set<String> roles;

    public AuthResponse(String accessToken, Set<String> roles) {
        this.accessToken = accessToken;
        this.roles = roles;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Set<String> getRoles() {
        return roles;
    }
}
