package com.dao;

import java.sql.*;
import com.util.ConnectionFactory;

public class NetworkDAO {

    public boolean followUser(int followerId, int followingId) {
        String sql = "INSERT INTO network (follower_id, following_id) VALUES (?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, followerId);
            pstmt.setInt(2, followingId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false; // Likely already following
        }
    }

    public boolean unfollowUser(int followerId, int followingId) {
        String sql = "DELETE FROM network WHERE follower_id = ? AND following_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, followerId);
            pstmt.setInt(2, followingId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}