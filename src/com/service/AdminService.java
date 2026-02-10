package com.service;

import com.dao.AdminDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdminService {
    // 1. Initialize Logger for professional tracking of admin actions
    private static final Logger logger = LogManager.getLogger(AdminService.class);
    
    // 2. Delegate data access to the AdminDAO
    private AdminDAO adminDAO = new AdminDAO();

    /**
     * Option 1: Global Analytics
     * Fetches platform-wide stats like total users and posts.
     */
    public void showPlatformStats() {
        logger.info("Admin accessing global platform analytics.");
        adminDAO.getPlatformStats();
    }

    /**
     * Option 2: Moderate Content
     * Allows an admin to delete any post regardless of ownership.
     */
    public void moderateContent(int postId) {
        logger.warn("Admin action: Attempting to moderate (delete) Post ID {}", postId);
        
        // This calls the special admin delete method in your DAO
        boolean success = adminDAO.deleteAnyPost(postId);
        
        if (success) {
            logger.info("Post ID {} successfully moderated by admin.", postId);
            System.out.println("✅ Success: Post ID " + postId + " has been moderated (deleted).");
        } else {
            logger.error("Moderation failed: Post ID {} not found.", postId);
            System.out.println("❌ Error: Could not find or delete Post ID " + postId);
        }
    }
}