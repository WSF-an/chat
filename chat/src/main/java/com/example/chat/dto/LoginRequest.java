package com.example.chat.dto;

public class LoginRequest {
    private String uname;
    private String password;

    // Getters 和 Setters
    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
