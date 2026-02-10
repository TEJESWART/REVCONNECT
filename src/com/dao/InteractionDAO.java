package com.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.model.Comment;
import com.util.ConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InteractionDAO {
    private static final Logger logger = LogManager.getLogger(InteractionDAO.class);

    /**
     * Checks if a comment exists before attempting deletion.
     */
    public boolean commentExists(int commentId) {
        String sql = "SELECT 1 FROM comments WHERE comment_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, commentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking comment existence for ID {}: {}", commentId, e.getMessage());
            return false;
        }
    }

    /**
     * Option 8: Add Like + Trigger Notification
     */
    public boolean addLike(int userId, int postId) {
        String sql = "INSERT IGNORE INTO likes (user_id, post_id) VALUES (?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, postId);
            
            if (pstmt.executeUpdate() > 0) {
                int ownerId = getPostOwnerId(postId);
                if (ownerId != -1 && ownerId != userId) { 
                    createNotification(ownerId, "User ID " + userId + " liked your post (Post ID: " + postId + ")");
                }
                return true;
            }
        } catch (SQLException e) { 
            logger.error("SQL Error in addLike: {}", e.getMessage()); 
        }
        return false;
    }

    /**
     * NEW: Option 22 - Unlike Post
     */
    public boolean unlikePost(int userId, int postId) {
        String sql = "DELETE FROM likes WHERE user_id = ? AND post_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, postId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error unliking post: " + e.getMessage());
            return false;
        }
    }

    /**
     * Option 9: Add Comment + Trigger Notification
     */
    public boolean addComment(int userId, int postId, String content) {
        String sql = "INSERT INTO comments (user_id, post_id, content) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, postId);
            pstmt.setString(3, content);
            
            if (pstmt.executeUpdate() > 0) {
                int ownerId = getPostOwnerId(postId);
                if (ownerId != -1 && ownerId != userId) {
                    createNotification(ownerId, "User ID " + userId + " commented: " + 
                                       (content.length() > 20 ? content.substring(0, 20) + "..." : content));
                }
                return true;
            }
        } catch (SQLException e) { 
            logger.error("SQL Error in addComment: {}", e.getMessage()); 
        }
        return false;
    }

    /**
     * NEW: Option 23 - Fetch all comments for a post thread
     * Used by displayAllComments in Service
     */
    public List<Comment> getAllCommentsForPost(int postId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.*, u.username FROM comments c " +
                     "JOIN users u ON c.user_id = u.id " +
                     "WHERE c.post_id = ? ORDER BY c.created_at ASC";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Comment c = new Comment();
                c.setCommentId(rs.getInt("comment_id"));
                c.setPostId(rs.getInt("post_id"));
                c.setUserId(rs.getInt("user_id"));
                c.setContent(rs.getString("content"));
                c.setCreatedAt(rs.getTimestamp("created_at"));
                c.setUsername(rs.getString("username"));
                comments.add(c);
            }
        } catch (SQLException e) {
            logger.error("Error fetching comments for post {}: {}", postId, e.getMessage());
        }
        return comments;
    }

    /**
     * Option 14: Delete Comment (Checks ownership)
     */
    public boolean deleteComment(int commentId, int userId) {
        String sql = "DELETE FROM comments WHERE comment_id = ? AND user_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, commentId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            logger.error("SQL Error in deleteComment: {}", e.getMessage()); 
            return false; 
        }
    }

    // --- INTERNAL HELPERS ---

    private void createNotification(int targetUserId, String message) {
        String sql = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, targetUserId);
            pstmt.setString(2, message);
            pstmt.executeUpdate();
        } catch (SQLException e) { 
            logger.error("Error creating notification: {}", e.getMessage()); 
        }
    }

    private int getPostOwnerId(int postId) {
        String sql = "SELECT user_id FROM posts WHERE post_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("user_id");
        } catch (SQLException e) { 
            logger.error("Error fetching post owner: {}", e.getMessage()); 
        }
        return -1;
    }
}