package com.service;

import com.dao.UserDAO;
import com.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthService {
    private static final Logger logger = LogManager.getLogger(AuthService.class);
    private UserDAO userDAO = new UserDAO();

    /**
     * Option 2: Login Logic
     */
    public User login(String username, String password) {
        logger.info("Login attempt initiated for username: {}", username);
        User user = userDAO.login(username, password);
        
        if (user != null) {
            logger.info("Login successful for user: {}", username);
        } else {
            logger.warn("Login failed for username: {}. Invalid credentials.", username);
        }
        return user;
    }

    /**
     * Standard Registration Logic (PERSONAL / CREATOR)
     */
    public String register(User user) {
        logger.info("Registering new standard user: {}", user.getUsername());
        boolean success = userDAO.registerUser(user);
        if (success) {
            logger.info("User {} registered successfully.", user.getUsername());
            return "Success";
        } else {
            logger.warn("Registration failed for user: {}", user.getUsername());
            return "Error: Username or Email might already exist.";
        }
    }

    /**
     * NEW: Enhanced Business Registration
     * Added a small validation check for mandatory fields.
     */
    public String registerBusiness(User user, String cat, String addr, String web, String hours) {
        // Validation: Ensure the category isn't empty before hitting the DB
        if (cat == null || cat.trim().isEmpty()) {
            return "Error: Business Category is mandatory.";
        }

        logger.info("Registering new Business account: {}", user.getUsername());
        boolean success = userDAO.registerBusinessAccount(user, cat, addr, web, hours);
        
        if (success) {
            logger.info("Business account for {} registered successfully.", user.getUsername());
            return "Business Account Created Successfully!";
        } else {
            logger.error("Business registration failed for user: {}", user.getUsername());
            return "Registration Failed: Database error.";
        }
    }

    /**
     * Option 7: Edit Bio
     */
    public String updateProfileBio(int userId, String bio) {
        logger.info("Updating bio for User ID: {}", userId);
        return userDAO.updateBio(userId, bio) ? "Bio Updated!" : "Update Failed.";
    }

    /**
     * Option 4: Profile Details
     * Now returns the User object containing both standard and business fields.
     */
    public User getProfile(int userId) {
        logger.info("Fetching profile details for User ID: {}", userId);
        return userDAO.getUserById(userId);
    }

    /**
     * Option 13: DELETE ACCT
     */
    public String deleteAccount(int userId) {
        logger.warn("Account deletion requested for User ID: {}", userId);
        return userDAO.deleteUser(userId) ? "Account Deleted Successfully." : "Error: Account not found.";
    }
}