package com.service;

import java.util.*;
import com.dao.PostDAO;
import com.dao.InteractionDAO; 
import com.dao.UserDAO; // Added this import
import com.model.Post;
import com.model.Comment; 
import com.model.User;    // Added this import
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PostService {
    private static final Logger logger = LogManager.getLogger(PostService.class);
    private PostDAO postDAO = new PostDAO();
    private InteractionDAO interactionDAO = new InteractionDAO(); 
    private UserDAO userDAO = new UserDAO(); // Added to handle user suggestions

    /**
     * Option 3: List all potential users to follow.
     * This makes Option 5 much easier because you can see the IDs!
     */
    public void showSuggestions(int currentUserId) {
        logger.info("Fetching follow suggestions for User ID {}.", currentUserId);
        List<User> users = userDAO.getAllUsersExcept(currentUserId);
        
        System.out.println("\n--- 👥 PEOPLE YOU MAY KNOW ---");
        if (users == null || users.isEmpty()) {
            System.out.println("No other users found at the moment.");
        } else {
            for (User u : users) {
                // Displays ID, Username, and Bio so you know who to follow
                System.out.println("ID: " + u.getId() + " | Username: " + u.getUsername() + " | Bio: " + (u.getBio() != null ? u.getBio() : "No bio yet"));
            }
            System.out.println("-------------------------------");
        }
    }

    /**
     * Option 1: Add a new post. 
     */
    public String addNewPost(int userId, String content) {
        logger.info("User ID {} attempting to create a post.", userId);
        if (content == null || content.trim().isEmpty()) {
            return "Content cannot be empty!";
        }
        boolean success = postDAO.createPost(new Post(userId, content));
        return success ? "Published!" : "Error publishing post.";
    }

    /**
     * Option 2: Displays the global feed with Likes and Comments.
     */
    public void displayFeed() {
        logger.info("Accessing global feed.");
        List<Post> posts = postDAO.getAllPosts();
        
        if (posts.isEmpty()) {
            System.out.println("The feed is empty. Be the first to post!");
        } else {
            System.out.println("\n--- 🌐 REVCONNECT GLOBAL FEED ---");
            for (Post p : posts) {
                System.out.println("[" + p.getPostId() + "] " + p.getUsername().toUpperCase() + ":");
                System.out.println("   \"" + p.getContent() + "\"");
                System.out.println("   ❤️ Likes: " + p.getLikes());

                List<Comment> comments = interactionDAO.getCommentsByPostId(p.getPostId());
                if (comments != null && !comments.isEmpty()) {
                    System.out.println("   💬 Comments:");
                    for (Comment c : comments) {
                        System.out.println("      └─> [ID: " + c.getCommentId() + "] " + c.getUsername() + ": " + c.getContent());
                    }
                } else {
                    System.out.println("   💬 (No comments yet)");
                }
                System.out.println("-------------------------------------------");
            }
        }
    }

    /**
     * Option 11: Delete Post with Ownership verification.
     */
    public String deletePost(int postId, int userId) {
        logger.warn("User ID {} is attempting to delete Post ID {}.", userId, postId);

        if (!postDAO.postExists(postId)) {
            logger.warn("Delete failed: Post ID {} does not exist.", postId);
            return "Notice: Post ID " + postId + " does not exist.";
        }

        boolean success = postDAO.deletePost(postId, userId);

        if (success) {
            logger.info("Post ID {} successfully deleted by owner (User ID {}).", postId, userId);
            return "Success: Post deleted.";
        } else {
            logger.warn("Unauthorized delete attempt: User ID {} is not the owner of Post ID {}.", userId, postId);
            return "Notice: You do not have permission to delete this post (Ownership required).";
        }
    }

    /**
     * Option 16: Trending Feed.
     */
    public void showTrending() {
        List<Post> trending = postDAO.getTrendingPosts();
        System.out.println("\n--- 🔥 TRENDING ---");
        for (Post p : trending) {
            System.out.println(p.getUsername() + " (" + p.getLikes() + " likes): " + p.getContent());
        }
    }

    /**
     * Option 17 & 18: Hashtags.
     */
    public void discoverHashtags(String hashtag) {
        List<Post> results = postDAO.searchPostsByHashtag(hashtag.replace("#", ""));
        for (Post p : results) {
            System.out.println("[" + p.getPostId() + "] " + p.getUsername() + ": " + p.getContent());
        }
    }

    public void showTrendingHashtags() {
        postDAO.getTopHashtags(); 
    }

    /**
     * Option 20 & 21: Bookmarks.
     */
    public String bookmarkPost(int userId, int postId) {
        return postDAO.savePost(userId, postId) ? "Saved!" : "Error.";
    }

    public void displaySavedPosts(int userId) {
        List<Post> saved = postDAO.getSavedPosts(userId);
        if (saved.isEmpty()) {
            System.out.println("You haven't saved any posts yet.");
        } else {
            for (Post p : saved) {
                System.out.println("[" + p.getPostId() + "] " + p.getUsername() + ": " + p.getContent());
            }
        }
    }

    public String sharePost(int userId, int postId) {
        return postDAO.sharePost(userId, postId) ? "Shared!" : "Error.";
    }
    
    public void showUserAnalytics(int userId, int followers, int following) {
        postDAO.getUserAnalytics(userId);
    }

    /**
     * Helper method for JUnit Tests
     */
    public int createTestPost(int userId, String content) {
        return postDAO.createPostAndReturnId(new Post(userId, content));
    }
}