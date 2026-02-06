package com.model;

import java.sql.Timestamp;

public class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private String userType;
    private String bio;
    private Timestamp lastLogin;
    private int followerCount; // Added to support the Profile view logic

    // Default Constructor
    public User() {}

    // Constructor for Registration
    public User(String username, String email, String password, String userType) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    // ID Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    // Username Getters & Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    // Email Getters & Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // Password Getters & Setters
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // UserType Getters & Setters
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    // Bio Getters & Setters
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    // Timestamp Getters & Setters
    public Timestamp getLastLogin() { return lastLogin; }
    public void setLastLogin(Timestamp lastLogin) { this.lastLogin = lastLogin; }

    // Follower Count Getters & Setters
    public int getFollowerCount() { return followerCount; }
    public void setFollowerCount(int followerCount) { this.followerCount = followerCount; }
}