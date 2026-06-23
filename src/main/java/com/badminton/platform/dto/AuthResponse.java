package com.badminton.platform.dto;

import com.badminton.platform.entity.User;

public class AuthResponse {

    private String accessToken;
    private User user;

    public AuthResponse(String accessToken, User user) {
        this.accessToken = accessToken;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public String getAccessToken() {
        return accessToken;
    }

}
