package com.dao;

import java.sql.*;
import com.util.ConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdminDAO {
    // 1. Initialize Logger for professional error tracking
    private static final Logger logger = LogManager.getLogger(AdminDAO.class);

    /**
     * Fetches comprehensive platform statistics for the Admin Dashboard.
     * Merges user counts, post counts, connection counts, and user type popularity.
     */
    public void getPlatformStats() {
        String sql = "SELECT " +
                     "(SELECT COUNT(*) FROM users) AS total_users, " +
                     "(SELECT COUNT(*) FROM posts) AS total_posts, " +
                     "(SELECT COUNT(*) FROM follows) AS total_connections, " +
                     "(SELECT user_type FROM users GROUP BY user_type ORDER BY COUNT(*) DESC LIMIT 1) AS popular_type";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                System.out.println("\n--- 🛠️ REVCONNECT GLOBAL ANALYTICS ---");
                System.out.println("Total Registered Users : " + rs.getInt("total_users"));
                System.out.println("Total Posts Published  : " + rs.getInt("total_posts"));
                System.out.println("Active Connections     : " + rs.getInt("total_connections"));
                System.out.println("Dominant User Type     : " + (rs.getString("popular_type") != null ? rs.getString("popular_type") : "N/A"));
                System.out.println("---------------------------------------");
            }
        } catch (SQLException e) {
            logger.error("Admin Analytics Error: " + e.getMessage()); // Logs the error professionally
        }
    }

    /**
     * Global Moderation: Deletes any post regardless of user ownership.
     * Used by AdminService for content moderation.
     */
    public boolean deleteAnyPost(int postId) {
        String sql = "DELETE FROM posts WHERE post_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, postId);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Admin successfully deleted Post ID: {}", postId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Admin Global Delete Error for Post ID {}: {}", postId, e.getMessage());
        }
        return false;
    }
}