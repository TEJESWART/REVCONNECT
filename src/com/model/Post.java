package com.model;

import java.sql.Timestamp;

public class Post {
    private int postId;
    private int userId;
    private String username; 
    private String content;
    private int likes;       
    private Timestamp createdAt;
    private String postType; // Values: "STANDARD", "ANNOUNCEMENT", or "PROMOTION"

    /**
     * 1. Default Constructor
     * Required by JDBC for ResultSets and generic object instantiation.
     */
    public Post() {
        this.postType = "STANDARD"; 
    }

    /**
     * 2. Standard Constructor
     * Used for basic posts where type isn't specified (defaults to STANDARD).
     */
    public Post(int userId, String content) {
        this.userId = userId;
        this.content = content;
        this.postType = "STANDARD";
    }

    /**
     * 3. Business Constructor
     * Specifically used by PostService.postBusinessUpdate to clear red marks.
     */
    public Post(int userId, String content, String postType) {
        this.userId = userId;
        this.content = content;
        this.postType = postType;
    }

    // --- Getters and Setters ---
    // These must exist so the DAO and Service can access the private fields

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

    /**
     * FIXED: getPostType() method
     */
    public String getPostType() { 
        return postType; 
    }

    /**
     * FIXED: setPostType() method
     */
    public void setPostType(String postType) { 
        this.postType = postType; 
    }
}