package com.service;

import com.dao.InteractionDAO;

public class InteractionService {
    private InteractionDAO interactionDAO = new InteractionDAO();

    public String likePost(int userId, int postId) {
        if (interactionDAO.addLike(userId, postId)) {
            return "Success: Post liked!";
        } else {
            return "Notice: Already liked or post doesn't exist.";
        }
    }

    public String commentOnPost(int userId, int postId, String content) {
        if (content == null || content.trim().isEmpty()) {
            return "Error: Comment cannot be empty.";
        }
        if (interactionDAO.addComment(userId, postId, content)) {
            return "Success: Comment added!";
        } else {
            return "Error: Could not add comment.";
        }
    }

    public String removeComment(int commentId, int userId) {
        // Matches the deleteComment method in InteractionDAO
        if (interactionDAO.deleteComment(commentId, userId)) {
            return "Success: Comment deleted successfully.";
        } else {
            return "Error: Could not delete comment. Verify ID and ownership.";
        }
    }
}