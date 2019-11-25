package com.travelinsurancemaster.model.security;

/**
 * Created by Chernov Artur on 13.05.15.
 */
public class LoginStatus {

    private final boolean loggedIn;
    private final String username;
    private final String role;

    public LoginStatus(boolean loggedIn, String username, String role) {
        this.loggedIn = loggedIn;
        this.username = username;
        this.role = role;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}