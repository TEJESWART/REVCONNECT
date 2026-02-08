package com.service;

import com.dao.InteractionDAO;
import com.dao.PostDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InteractionService {
    private static final Logger logger = LogManager.getLogger(InteractionService.class);
    private InteractionDAO interactionDAO = new InteractionDAO();
    private PostDAO postDAO = new PostDAO();

    public String likePost(int userId, int postId) {
        logger.info("User ID {} attempting to like Post ID {}.", userId, postId);
        if (!postDAO.postExists(postId)) {
            logger.warn("Like failed: Post ID {} does not exist.", postId);
            return "Failed: Post ID " + postId + " does not exist.";
        }
        boolean success = interactionDAO.addLike(userId, postId);
        if (success) {
            logger.info("Like successfully recorded for Post ID {} by User ID {}.", postId, userId);
            return "Post Liked!";
        } else {
            logger.warn("Like failed: User ID {} may have already liked Post ID {}.", userId, postId);
            return "Notice: You have already liked this post.";
        }
    }

    public String commentOnPost(int userId, int postId, String content) {
        logger.info("User ID {} is commenting on Post ID {}.", userId, postId);
        if (content == null || content.trim().isEmpty()) {
            return "Comment cannot be empty!";
        }
        if (!postDAO.postExists(postId)) {
            return "Failed: Cannot comment on a post that doesn't exist.";
        }
        boolean success = interactionDAO.addComment(userId, postId, content);
        if (success) {
            return "Comment added!";
        } else {
            logger.error("Database error adding comment for User ID {} on Post ID {}.", userId, postId);
            return "Error adding comment.";
        }
    }

    /**
     * Updated Dashboard Option 14: Delete a comment with specific feedback.
     */
    public String removeComment(int commentId, int userId) {
        logger.warn("Deletion request for Comment ID {} by User ID {}.", commentId, userId);
        
        // 1. Check if the comment even exists in the database
        if (!interactionDAO.commentExists(commentId)) {
            logger.warn("Deletion failed: Comment ID {} does not exist.", commentId);
            return "Notice: Comment ID " + commentId + " does not exist.";
        }

        // 2. Try to delete (the DAO SQL requires BOTH comment_id and user_id to match)
        boolean success = interactionDAO.deleteComment(commentId, userId);
        
        if (success) {
            logger.info("Comment ID {} deleted successfully.", commentId);
            return "Success: Comment deleted successfully.";
        } else {
            // If it exists (Step 1 passed) but delete failed (Step 2), it means the user_id didn't match
            logger.warn("Unauthorized deletion attempt: User ID {} is not the owner of Comment ID {}.", userId, commentId);
            return "Notice: You do not have permission to delete this comment (Ownership required).";
        }
    }
}