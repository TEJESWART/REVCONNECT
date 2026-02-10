package com.service;

import com.dao.InteractionDAO;
import com.dao.PostDAO;
import com.model.Comment; // ENSURE THIS IMPORT EXISTS
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InteractionService {
    private static final Logger logger = LogManager.getLogger(InteractionService.class);
    private InteractionDAO interactionDAO = new InteractionDAO();
    private PostDAO postDAO = new PostDAO();

    /**
     * Option 8: Like a Post
     */
    public String likePost(int userId, int postId) {
        logger.info("User ID {} attempting to like Post ID {}.", userId, postId);
        if (!postDAO.postExists(postId)) {
            logger.warn("Like failed: Post ID {} does not exist.", postId);
            return "❌ Failed: Post ID " + postId + " does not exist.";
        }
        boolean success = interactionDAO.addLike(userId, postId);
        if (success) {
            logger.info("Like successfully recorded for Post ID {} by User ID {}.", postId, userId);
            return "✅ Post Liked!";
        } else {
            logger.warn("Like failed: User ID {} may have already liked Post ID {}.", userId, postId);
            return "Notice: You have already liked this post.";
        }
    }

    /**
     * NEW: Option 22 - Unlike a Post
     */
    public String unlikePost(int userId, int postId) {
        logger.info("User ID {} attempting to unlike Post ID {}.", userId, postId);
        // It's good practice to check if the post exists here too
        if (!postDAO.postExists(postId)) {
            return "❌ Failed: Post ID " + postId + " does not exist.";
        }
        boolean success = interactionDAO.unlikePost(userId, postId);
        if (success) {
            logger.info("Post ID {} unliked by User ID {}.", postId, userId);
            return "✅ Post unliked successfully!";
        } else {
            logger.warn("Unlike failed: User ID {} may not have liked Post ID {}.", userId, postId);
            return "❌ You haven't liked this post yet.";
        }
    }

    /**
     * Option 9: Comment on a Post
     */
    public String commentOnPost(int userId, int postId, String content) {
        logger.info("User ID {} is commenting on Post ID {}.", userId, postId);
        if (content == null || content.trim().isEmpty()) {
            return "Comment cannot be empty!";
        }
        if (!postDAO.postExists(postId)) {
            return "❌ Failed: Cannot comment on a post that doesn't exist.";
        }
        boolean success = interactionDAO.addComment(userId, postId, content);
        if (success) {
            return "✅ Comment added!";
        } else {
            logger.error("Database error adding comment for User ID {} on Post ID {}.", userId, postId);
            return "Error adding comment.";
        }
    }

    /**
     * NEW: Option 23 - Displays all comments in a clean thread format.
     */
    public void displayAllComments(int postId) {
        logger.info("Fetching all comments for post ID: {}", postId);
        
        // Ensure this method name matches exactly what is in InteractionDAO
        List<Comment> comments = interactionDAO.getAllCommentsForPost(postId);
        
        System.out.println("\n--- 💬 FULL COMMENT THREAD (Post #" + postId + ") ---");
        if (comments == null || comments.isEmpty()) {
            System.out.println("No comments found for this post.");
        } else {
            for (Comment c : comments) {
                // Using getters from your Comment model
                String author = (c.getUsername() != null) ? c.getUsername().toUpperCase() : "UNKNOWN";
                System.out.println("[" + author + " at " + c.getCreatedAt() + "]");
                System.out.println("  └─ " + c.getContent());
            }
        }
        System.out.println("----------------------------------------------");
    }

    /**
     * Option 14: Delete a comment with ownership check.
     */
    public String removeComment(int commentId, int userId) {
        logger.warn("Deletion request for Comment ID {} by User ID {}.", commentId, userId);
        
        // Ensure interactionDAO has commentExists method
        if (!interactionDAO.commentExists(commentId)) {
            logger.warn("Deletion failed: Comment ID {} does not exist.", commentId);
            return "Notice: Comment ID " + commentId + " does not exist.";
        }

        // Ensure interactionDAO has deleteComment method
        boolean success = interactionDAO.deleteComment(commentId, userId);
        
        if (success) {
            logger.info("Comment ID {} deleted successfully.", commentId);
            return "✅ Success: Comment deleted successfully.";
        } else {
            logger.warn("Unauthorized deletion attempt: User ID {} is not the owner of Comment ID {}.", userId, commentId);
            return "Notice: You do not have permission to delete this comment (Ownership required).";
        }
    }
}