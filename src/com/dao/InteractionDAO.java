package com.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.model.Comment;
import com.util.ConnectionFactory;

public class InteractionDAO {

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
                // Fetch post owner and notify them
                int ownerId = getPostOwnerId(postId);
                if (ownerId != -1 && ownerId != userId) { 
                    createNotification(ownerId, "User ID " + userId + " liked your post (Post ID: " + postId + ")");
                }
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
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
                // Fetch post owner and notify them
                int ownerId = getPostOwnerId(postId);
                if (ownerId != -1 && ownerId != userId) {
                    createNotification(ownerId, "User ID " + userId + " commented: " + 
                                       (content.length() > 20 ? content.substring(0, 20) + "..." : content));
                }
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /**
     * Internal Helper: Creates the notification entry
     */
    private void createNotification(int targetUserId, String message) {
        String sql = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, targetUserId);
            pstmt.setString(2, message);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Internal Helper: Finds who to notify
     */
    private int getPostOwnerId(int postId) {
        String sql = "SELECT user_id FROM posts WHERE post_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("user_id");
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    /**
     * Option 2: Fetch Comments for Feed
     */
    public List<Comment> getCommentsByPostId(int postId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.*, u.username FROM comments c " +
                     "JOIN users u ON c.user_id = u.id " +
                     "WHERE c.post_id = ? ORDER BY c.created_at ASC";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Comment comment = new Comment();
                comment.setCommentId(rs.getInt("comment_id")); 
                comment.setPostId(rs.getInt("post_id"));
                comment.setUserId(rs.getInt("user_id"));
                comment.setContent(rs.getString("content"));
                comment.setCreatedAt(rs.getTimestamp("created_at"));
                comment.setUsername(rs.getString("username")); 
                comments.add(comment);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return comments;
    }

    /**
     * Option 14: Delete Comment
     */
    public boolean deleteComment(int commentId, int userId) {
        String sql = "DELETE FROM comments WHERE comment_id = ? AND user_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, commentId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}