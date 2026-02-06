package com.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.model.User;
import com.util.ConnectionFactory;

/**
 * Data Access Object for User-related database operations.
 * Handles Login, Registration, Following, and Suggestions.
 */
public class UserDAO {

    /**
     * 1. Fetch user by ID
     */
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email")); // Added missing email field
                u.setBio(rs.getString("bio"));
                u.setUserType(rs.getString("user_type"));
                u.setPassword(rs.getString("password")); // Added to prevent AuthService null errors
                return u;
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return null;
    }

    /**
     * 2. Login Logic
     */
    public User login(String username, String password) {
        String sql = "SELECT id FROM users WHERE username = ? AND password = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return getUserById(rs.getInt("id"));
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return null;
    }

    /**
     * 3. Register Logic
     */
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (username, email, password, user_type) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getUserType());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            System.err.println("Registration Error: " + e.getMessage());
            return false; 
        }
    }

    /**
     * 4. Update Bio
     */
    public boolean updateBio(int userId, String bio) {
        String sql = "UPDATE users SET bio = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bio);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false; 
        }
    }

    /**
     * 5. Delete User
     */
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false; 
        }
    }

    /**
     * 6. Follow Logic
     */
    public boolean followUser(int followerId, int targetId) {
        String sql = "INSERT IGNORE INTO follows (follower_id, following_id) VALUES (?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, followerId);
            pstmt.setInt(2, targetId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            return false; 
        }
    }

    /**
     * 7. Count Followers
     */
    public int getFollowerCount(int userId) {
        String sql = "SELECT COUNT(*) FROM follows WHERE following_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return 0;
    }

    /**
     * 8. Count Following
     */
    public int getFollowingCount(int userId) {
        String sql = "SELECT COUNT(*) FROM follows WHERE follower_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return 0;
    }

    /**
     * 9. Search Users
     */
    public List<User> searchUsers(String query, int excludeId) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, user_type FROM users WHERE username LIKE ? AND id != ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + query + "%");
            pstmt.setInt(2, excludeId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setUserType(rs.getString("user_type"));
                users.add(u);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return users;
    }

    /**
     * 10. Social Suggestions
     */
    public List<User> getSuggestions(int userId, String type) {
        List<User> suggestions = new ArrayList<>();
        String targetType = type.equalsIgnoreCase("PERSONAL") ? "CREATOR" : "PERSONAL";
        
        String sql = "SELECT id, username, user_type FROM users " +
                     "WHERE user_type = ? AND id != ? " +
                     "AND id NOT IN (SELECT following_id FROM follows WHERE follower_id = ?) " +
                     "LIMIT 5";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, targetType);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setUserType(rs.getString("user_type"));
                suggestions.add(u);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return suggestions;
    }
}