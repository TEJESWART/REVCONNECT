package com.service;

import java.sql.*;
import com.util.ConnectionFactory;

public class NotificationService {
    
    /**
     * Fetches and displays all notifications for the user.
     * Automatically marks them as 'read' after they are shown.
     */
    public void showNotifications(int userId) {
        String sql = "SELECT message, created_at FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            System.out.println("\n--- YOUR NOTIFICATIONS ---");
            boolean hasNotes = false;
            while (rs.next()) {
                hasNotes = true;
                System.out.println("[" + rs.getTimestamp("created_at") + "] " + rs.getString("message"));
            }
            
            if (!hasNotes) {
                System.out.println("No notifications found.");
            } else {
                // Once viewed, mark all as read so the dashboard count resets
                markAsRead(userId);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the number of notifications that have not been viewed yet.
     * Use this in App.java to show an alert like "DASHBOARD (3 NEW!)"
     */
    public int getUnreadCount(int userId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = FALSE";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Internal helper to update notification status in the database.
     */
    private void markAsRead(int userId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE user_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}