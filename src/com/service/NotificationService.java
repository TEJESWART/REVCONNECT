package com.service;

import com.dao.NotificationDAO;
import com.model.Notification;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NotificationService {
    // 1. Initialize Logger for consistency with other services
    private static final Logger logger = LogManager.getLogger(NotificationService.class);
    
    // 2. Delegate data access to the DAO
    private NotificationDAO notificationDAO = new NotificationDAO();

    /**
     * Used for the Dashboard alert: "[3 NEW!]"
     * Returns the number of notifications that have not been viewed yet.
     */
    public int getUnreadCount(int userId) {
        logger.info("Checking unread notifications for User ID: {}", userId);
        return notificationDAO.getUnreadCount(userId);
    }

    /**
     * Displays all notifications and marks them as read
     * Fetches the list from the DAO and prints them to the console.
     */
    public void showNotifications(int userId) {
        logger.info("Fetching and displaying notifications for User ID: {}", userId);
        List<Notification> list = notificationDAO.getNotificationsForUser(userId);
        
        if (list == null || list.isEmpty()) {
            System.out.println("No new notifications found.");
        } else {
            System.out.println("\n--- 🔔 YOUR NOTIFICATIONS ---");
            for (Notification n : list) {
                // Display timestamp and message for a professional look
                System.out.println("-> [" + n.getCreatedAt() + "] " + n.getMessage());
            }
            
            // Once viewed, mark them as read in the DB so the dashboard count resets
            boolean marked = notificationDAO.markAsRead(userId);
            if (marked) {
                logger.info("Notifications marked as read for User ID: {}", userId);
            }
        }
    }
}