package com.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.model.User;
import com.util.ConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Data Access Object for User-related database operations.
 * Handles Login, Registration, Following, and Enhanced Business Profiles.
 */
public class UserDAO {
    private static final Logger logger = LogManager.getLogger(UserDAO.class);

    /**
     * Register Business Account (Transaction-Based)
     */
    public boolean registerBusinessAccount(User user, String cat, String addr, String web, String hours) {
        String userSql = "INSERT INTO users (username, email, password, user_type) VALUES (?, ?, ?, ?)";
        String bizSql = "INSERT INTO business_profiles (user_id, category, address, website_url, business_hours) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false); 

            PreparedStatement pstmt1 = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            pstmt1.setString(1, user.getUsername());
            pstmt1.setString(2, user.getEmail());
            pstmt1.setString(3, user.getPassword());
            pstmt1.setString(4, "BUSINESS");
            pstmt1.executeUpdate();

            ResultSet rs = pstmt1.getGeneratedKeys();
            if (rs.next()) {
                int newUserId = rs.getInt(1);

                PreparedStatement pstmt2 = conn.prepareStatement(bizSql);
                pstmt2.setInt(1, newUserId);
                pstmt2.setString(2, cat);
                pstmt2.setString(3, addr);
                pstmt2.setString(4, web);
                pstmt2.setString(5, hours);
                pstmt2.executeUpdate();
            }

            conn.commit(); 
            logger.info("Business account for {} registered successfully.", user.getUsername());
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); 
                    logger.warn("Transaction Rolled Back: Business registration failed.");
                } catch (SQLException ex) {
                    logger.error("Rollback Error: {}", ex.getMessage());
                }
            }
            logger.error("Registration Error: {}", e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) { }
            }
        }
    }

    /**
     * Standard Registration Logic
     */
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (username, email, password, user_type) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getUserType().toUpperCase());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            logger.error("Registration Error: {}", e.getMessage());
            return false; 
        }
    }

    /**
     * Login Logic
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
            logger.error("Login Error: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 1. Fetch user by ID - UPDATED WITH LEFT JOIN
     * Now retrieves standard info AND business details if they exist.
     */
    public User getUserById(int id) {
        String sql = "SELECT u.*, b.category, b.address, b.website_url, b.business_hours " +
                     "FROM users u " +
                     "LEFT JOIN business_profiles b ON u.id = b.user_id " +
                     "WHERE u.id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                u.setBio(rs.getString("bio"));
                u.setUserType(rs.getString("user_type"));
                u.setPassword(rs.getString("password"));
                
                // Note: Ensure your User.java model has these fields and setters:
                u.setCategory(rs.getString("category"));
                u.setAddress(rs.getString("address"));
                u.setWebsite(rs.getString("website_url"));
                u.setHours(rs.getString("business_hours"));
                
                return u;
            }
        } catch (SQLException e) { 
            logger.error("Error fetching profile for ID {}: {}", id, e.getMessage()); 
        }
        return null;
    }

    /**
     * 4. Edit Bio
     */
    public boolean updateBio(int userId, String bio) {
        String sql = "UPDATE users SET bio = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bio);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            logger.error("Update Bio Error: {}", e.getMessage());
            return false; 
        }
    }

    /**
     * 5. DELETE ACCT
     */
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            logger.error("Delete User Error: {}", e.getMessage());
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
            logger.error("Follow Error: {}", e.getMessage());
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
            logger.error("Follower Count Error: {}", e.getMessage());
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
            logger.error("Following Count Error: {}", e.getMessage());
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
            logger.error("Search Users Error: {}", e.getMessage());
        }
        return users;
    }

    /**
     * Suggestions Helper
     */
    public List<User> getAllUsersExcept(int currentUserId) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, bio FROM users WHERE id != ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setBio(rs.getString("bio"));
                users.add(u);
            }
        } catch (SQLException e) {
            logger.error("Error in getAllUsersExcept: {}", e.getMessage());
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
            logger.error("Get Suggestions Error: {}", e.getMessage());
        }
        return suggestions;
    }
}