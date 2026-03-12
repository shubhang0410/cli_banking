package com.banking.model;

import java.time.LocalDateTime;

public class User {
    private int userId;
    private String fullName;
    private String email;
    private String password;
    private LocalDateTime createdAt;

    public User() {}

    public User(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email    = email;
        this.password = password;
    }

    // Getters & Setters
    public int getUserId()                      { return userId; }
    public void setUserId(int userId)           { this.userId = userId; }

    public String getFullName()                 { return fullName; }
    public void setFullName(String fullName)    { this.fullName = fullName; }

    public String getEmail()                    { return email; }
    public void setEmail(String email)          { this.email = email; }

    public String getPassword()                 { return password; }
    public void setPassword(String password)    { this.password = password; }

    public LocalDateTime getCreatedAt()                     { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)       { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "User{id=" + userId + ", name='" + fullName + "', email='" + email + "'}";
    }
}
