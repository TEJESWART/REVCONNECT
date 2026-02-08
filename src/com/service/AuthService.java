package com.service;

import com.dao.UserDAO;
import com.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthService {
    // 1. Initialize the Logger for this service
    private static final Logger logger = LogManager.getLogger(AuthService.class);
    private UserDAO userDAO = new UserDAO();

    public User login(String username, String password) {
        // Option 2: Login
        logger.info("Login attempt initiated for username: {}", username);
        User user = userDAO.login(username, password);
        
        if (user != null) {
            logger.info("Login successful for user: {}", username);
        } else {
            logger.warn("Login failed for username: {}. Invalid credentials.", username);
        }
        return user;
    }

    public String register(User user) {
        // Registration Logic
        logger.info("Registering new user: {}", user.getUsername());
        boolean success = userDAO.registerUser(user);
        if (success) {
            logger.info("User {} registered successfully.", user.getUsername());
            return "Success";
        } else {
            // CHANGED: Using warn instead of error to keep console clean
            logger.warn("Registration failed for user: {}", user.getUsername());
            return "Error";
        }
    }

    public String updateProfileBio(int userId, String bio) {
        // Option 7: Edit Bio
        logger.info("Updating bio for User ID: {}", userId);
        boolean success = userDAO.updateBio(userId, bio);
        if (success) {
            logger.info("Bio updated successfully for User ID: {}", userId);
            return "Bio Updated!";
        } else {
            // CHANGED: Using warn instead of error
            logger.warn("Bio update failed for User ID: {}", userId);
            return "Update Failed.";
        }
    }

    public User getProfile(int userId) {
        // Option 4: Profile
        logger.info("Fetching profile details for User ID: {}", userId);
        return userDAO.getUserById(userId);
    }

    public String deleteAccount(int userId) {
        // Option 13: DELETE ACCT
        logger.warn("Account deletion requested for User ID: {}", userId);
        boolean success = userDAO.deleteUser(userId);
        
        if (success) {
            logger.info("Account for User ID: {} deleted successfully.", userId);
            return "Account Deleted Successfully.";
        } else {
            // FIXED: Changed logger.error to logger.warn to remove red text from console
            logger.warn("Deletion notice: Account for User ID: {} not found or could not be deleted.", userId);
            return "Error.";
        }
    }
}