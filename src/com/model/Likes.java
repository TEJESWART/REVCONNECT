package com.model;

/**
 * Model class representing a 'Like' interaction on a post.
 */
public class Likes {
    private int likeId;
    private int postId;
    private int userId;

    public Likes() {}

    public Likes(int postId, int userId) {
        this.postId = postId;
        this.userId = userId;
    }

    // Getters and Setters
    public int getLikeId() {
        return likeId;
    }

    public void setLikeId(int likeId) {
        this.likeId = likeId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}