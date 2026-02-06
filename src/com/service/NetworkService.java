package com.service;

import com.dao.UserDAO;
import com.model.User;
import java.util.List;

/**
 * Service class handling social networking features.
 * Bridges the App Controller and UserDAO for follows, searches, and suggestions.
 */
public class NetworkService {
    private UserDAO userDAO = new UserDAO();

    /**
     * Logic for following another user.
     * Prevents self-following and calls the DAO.
     */
    public String follow(int followerId, int targetId) {
        if (followerId == targetId) {
            return "Error: You cannot follow yourself!";
        }
        // Logic to check if user exists or is already followed is handled in DAO via INSERT IGNORE
        boolean success = userDAO.followUser(followerId, targetId);
        return success ? "Success: You are now following User ID " + targetId : "Follow failed: User already followed or invalid ID.";
    }

    /**
     * Retrieves the number of users following this account.
     */
    public int getFollowerCount(int id) { 
        return userDAO.getFollowerCount(id); 
    }

    /**
     * Retrieves the number of users this account is following.
     */
    public int getFollowingCount(int id) { 
        return userDAO.getFollowingCount(id); 
    }

    /**
     * Search for users by username string.
     */
    public void findUsers(String query, int excludeId) {
        List<User> results = userDAO.searchUsers(query, excludeId);
        if (results == null || results.isEmpty()) {
            System.out.println("No users found matching: " + query);
        } else {
            System.out.println("\n--- 🔍 SEARCH RESULTS ---");
            for (User u : results) {
                System.out.println("ID: " + u.getId() + " | Name: " + u.getUsername() + " [" + u.getUserType() + "]");
            }
            System.out.println("-------------------------");
        }
    }

    /**
     * Social Discovery: Suggests accounts based on user type.
     */
    public void showSuggestions(int id, String type) {
        // Validation for safety
        if (type == null) type = "PERSONAL";
        
        // Fetches the list from the database
        List<User> suggestions = userDAO.getSuggestions(id, type);
        
        if (suggestions == null || suggestions.isEmpty()) {
            System.out.println("\nNo new suggestions at the moment! Try searching for users manually.");
        } else {
            System.out.println("\n--- ✨ SUGGESTED ACCOUNTS FOR YOU ---");
            for (User u : suggestions) {
                System.out.println("ID: " + u.getId() + " | Name: " + u.getUsername() + " [" + u.getUserType() + "]");
            }
            System.out.println("--------------------------------------");
            System.out.println("Tip: Use Option 5 and enter the ID to follow them!");
        }
    }
}