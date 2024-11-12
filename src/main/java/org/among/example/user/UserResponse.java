package org.among.example.user;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

public class UserResponse {
    private String username;
    private List<String> roles;

    public UserResponse(String username, List<String> roles) {
        this.username = username;
        this.roles = roles;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getRoles() {
        return roles;
    }
}
