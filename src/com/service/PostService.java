package com.service;

import java.util.*;
import com.dao.PostDAO;
import com.dao.InteractionDAO; 
import com.dao.UserDAO;
import com.model.Post;
import com.model.Comment; 
import com.model.User; 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PostService {
    private static final Logger logger = LogManager.getLogger(PostService.class);
    private PostDAO postDAO = new PostDAO();
    private InteractionDAO interactionDAO = new InteractionDAO(); 
    private UserDAO userDAO = new UserDAO();

    /**
     * Logic for Business Accounts only.
     * RED MARK FIX: Ensure Post.java has constructor: Post(int, String, String)
     */
    public String postBusinessUpdate(User user, String content, String type) {
        if (!"BUSINESS".equalsIgnoreCase(user.getUserType())) {
            return "❌ Error: Only Business accounts can post announcements or promotions.";
        }
        
        // This line will be red if the 3-parameter constructor is missing in Post.java
        Post businessPost = new Post(user.getId(), content, type);
        
        // This line will be red if createBusinessPost is missing in PostDAO.java
        boolean success = postDAO.createBusinessPost(businessPost);
        
        if (success) {
            return "📢 " + type + " published successfully!";
        }
        return "❌ Failed to publish business update.";
    }

    public void displayUserOnlyFeed(int userId) {
        List<Post> posts = postDAO.getOnlyUserPosts(userId);
        if (posts.isEmpty()) {
            System.out.println("\nYou haven't posted anything yet!");
        } else {
            System.out.println("\n--- 👤 MY POSTS ONLY ---");
            renderFeed(posts);
        }
    }

    public Post getTopPost(int userId) {
        return postDAO.getTopUserPost(userId);
    }

    public void displayPersonalFeed(int userId) {
        List<Post> posts = postDAO.getFollowedFeed(userId);
        if (posts.isEmpty()) {
            System.out.println("\nYour feed is quiet...");
        } else {
            System.out.println("\n--- 📱 YOUR PERSONAL FEED ---");
            renderFeed(posts);
        }
    }

    public void displayFeed() {
        List<Post> posts = postDAO.getAllPosts();
        if (posts.isEmpty()) {
            System.out.println("The feed is empty.");
        } else {
            System.out.println("\n--- 🌐 REVCONNECT GLOBAL FEED ---");
            renderFeed(posts);
        }
    }

    /**
     * RED MARK FIX: Ensure InteractionDAO has getAllCommentsForPost(int)
     */
    private void renderFeed(List<Post> posts) {
        for (Post p : posts) {
            String typeLabel = "";
            String border = "-------------------------------------------";
            
            // RED MARK FIX: Ensure Post.java has getPostType()
            if ("ANNOUNCEMENT".equalsIgnoreCase(p.getPostType())) {
                typeLabel = " [📢 ANNOUNCEMENT]";
                border = "*******************************************";
            } else if ("PROMOTION".equalsIgnoreCase(p.getPostType())) {
                typeLabel = " [💰 PROMOTED]";
                border = "###########################################";
            }

            if (!typeLabel.isEmpty()) System.out.println(border);
            System.out.println("[" + p.getPostId() + "]" + typeLabel + " " + p.getUsername().toUpperCase() + ":");
            System.out.println("   \"" + p.getContent() + "\"");
            System.out.println("   ❤️ Likes: " + p.getLikes());

            List<Comment> comments = interactionDAO.getAllCommentsForPost(p.getPostId());
            
            if (comments != null && !comments.isEmpty()) {
                System.out.println("   💬 Comments:");
                for (Comment c : comments) {
                    System.out.println("      └─> [ID: " + c.getCommentId() + "] " + c.getUsername() + ": " + c.getContent());
                }
            }
            System.out.println(border);
        }
    }

    public String addNewPost(int userId, String content) {
        if (content == null || content.trim().isEmpty()) return "Content cannot be empty!";
        return postDAO.createPost(new Post(userId, content)) ? "Published!" : "Error.";
    }

    public String deletePost(int postId, int userId) {
        if (!postDAO.postExists(postId)) return "Notice: Post ID " + postId + " does not exist.";
        return postDAO.deletePost(postId, userId) ? "Success: Post deleted." : "Notice: Permission denied.";
    }

    public void showTrending() {
        List<Post> trending = postDAO.getTrendingPosts();
        System.out.println("\n--- 🔥 TRENDING ---");
        for (Post p : trending) {
            System.out.println(p.getUsername() + " (" + p.getLikes() + " likes): " + p.getContent());
        }
    }

    public void discoverHashtags(String hashtag) {
        List<Post> results = postDAO.searchPostsByHashtag(hashtag.replace("#", ""));
        for (Post p : results) {
            System.out.println("[" + p.getPostId() + "] " + p.getUsername() + ": " + p.getContent());
        }
    }

    public void showTrendingHashtags() {
        postDAO.getTopHashtags(); 
    }

    public String bookmarkPost(int userId, int postId) {
        return postDAO.savePost(userId, postId) ? "Saved!" : "Error.";
    }

    public void displaySavedPosts(int userId) {
        List<Post> saved = postDAO.getSavedPosts(userId);
        if (saved.isEmpty()) {
            System.out.println("No saved posts.");
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
}