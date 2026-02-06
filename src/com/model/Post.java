package com.model;

import java.sql.Timestamp;

public class Post {
    private int postId;
    private int userId;
    private String username; // Added to show who wrote the post in the feed
    private String content;
    private int likes;       // Added to track popularity
    private Timestamp createdAt;

    // Default Constructor
    public Post() {}

    // Constructor for creating a new post
    public Post(int userId, String content) {
        this.userId = userId;
        this.content = content;
    }

    // Getters and Setters
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}