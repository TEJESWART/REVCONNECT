package com.service;

import java.util.*;
import java.util.stream.Collectors;
import com.dao.PostDAO;
import com.dao.InteractionDAO; 
import com.model.Post;
import com.model.Comment; 

public class PostService {
    private PostDAO postDAO = new PostDAO();
    private InteractionDAO interactionDAO = new InteractionDAO(); 

    /**
     * Handles Dashboard Option 1: Adds a new post.
     */
    public String addNewPost(int userId, String content) {
        if (content == null || content.trim().isEmpty()) {
            return "Post content cannot be empty!";
        }
        
        Post post = new Post(userId, content);
        return postDAO.createPost(post) ? "Post created successfully!" : "Failed to create post.";
    }

    /**
     * Handles Dashboard Option 2: Displays the global feed.
     */
    public void displayFeed() {
        List<Post> posts = postDAO.getAllPosts();
        
        if (posts.isEmpty()) {
            System.out.println("The feed is empty. Be the first to post!");
        } else {
            System.out.println("\n--- REVCONNECT FEED ---");
            for (Post p : posts) {
                System.out.println("[" + p.getPostId() + "] " + p.getUsername() + ": " + p.getContent());
                System.out.println("Likes: " + p.getLikes() + " | Posted on: " + p.getCreatedAt());

                List<Comment> comments = interactionDAO.getCommentsByPostId(p.getPostId());
                if (!comments.isEmpty()) {
                    System.out.println("   Comments:");
                    for (Comment c : comments) {
                        System.out.println("   -> [ID: " + c.getCommentId() + "] " + c.getUsername() + ": " + c.getContent());
                    }
                } else {
                    System.out.println("   (No comments yet)");
                }
                
                System.out.println("--------------------------------");
            }
        }
    }

    /**
     * NEW FEATURE: Option 20 - Logic to Bookmark a Post.
     */
    public String bookmarkPost(int userId, int postId) {
        return postDAO.savePost(userId, postId) ? "Post saved to your bookmarks!" : "Error: Could not save post. (It might already be saved).";
    }

    /**
     * NEW FEATURE: Option 21 - Logic to Display Saved Posts.
     */
    public void displaySavedPosts(int userId) {
        List<Post> saved = postDAO.getSavedPosts(userId);
        System.out.println("\n--- 🔖 MY SAVED POSTS ---");
        if (saved.isEmpty()) {
            System.out.println("You haven't saved any posts yet.");
        } else {
            for (Post p : saved) {
                System.out.println("[" + p.getPostId() + "] " + p.getUsername() + ": " + p.getContent());
                System.out.println("--------------------");
            }
        }
    }

    /**
     * Handles Dashboard Option 19 - User Analytics Dashboard.
     */
    public void showUserAnalytics(int userId, int followers, int following) {
        postDAO.getUserAnalytics(userId);
        System.out.println("Followers Count       : " + followers);
        System.out.println("Following Count       : " + following);
        System.out.println("-----------------------");
    }

    /**
     * Handles Dashboard Option 16 - Displays Trending Posts.
     */
    public void showTrending() {
        List<Post> trending = postDAO.getTrendingPosts();
        System.out.println("\n--- 🔥 TRENDING TOP 5 ---");
        
        if (trending.isEmpty()) {
            System.out.println("No interactions yet.");
        } else {
            for (int i = 0; i < trending.size(); i++) {
                Post p = trending.get(i);
                System.out.println((i + 1) + ". [" + p.getUsername() + "] " + p.getContent());
                System.out.println("   Engagement Score: " + p.getLikes() + " Likes");
                System.out.println("   --------------------");
            }
        }
    }

    /**
     * Handles Dashboard Option 17 - Hashtag Discovery.
     */
    public void discoverHashtags(String hashtag) {
        String cleanTag = hashtag.replace("#", "");
        List<Post> results = postDAO.searchPostsByHashtag(cleanTag);
        
        System.out.println("\n--- 🔍 DISCOVERING #" + cleanTag.toUpperCase() + " ---");
        
        if (results.isEmpty()) {
            System.out.println("No posts found with that hashtag.");
        } else {
            for (Post p : results) {
                System.out.println("[" + p.getPostId() + "] " + p.getUsername() + ": " + p.getContent());
                System.out.println("Posted on: " + p.getCreatedAt());
                System.out.println("--------------------");
            }
        }
    }

    /**
     * Handles Dashboard Option 18 - Trending Hashtags.
     */
    public void showTrendingHashtags() {
        List<String> contents = postDAO.getAllPostContents();
        Map<String, Integer> hashtagCounts = new HashMap<>();

        for (String content : contents) {
            String[] words = content.split("\\s+");
            for (String word : words) {
                if (word.startsWith("#") && word.length() > 1) {
                    String tag = word.toLowerCase().replaceAll("[^#a-zA-Z0-9]", "");
                    hashtagCounts.put(tag, hashtagCounts.getOrDefault(tag, 0) + 1);
                }
            }
        }

        System.out.println("\n--- 🔥 TRENDING HASHTAGS ---");
        if (hashtagCounts.isEmpty()) {
            System.out.println("No hashtags found yet.");
        } else {
            hashtagCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> System.out.println(entry.getKey() + " (" + entry.getValue() + " posts)"));
        }
    }

    /**
     * Handles Dashboard Option 10: Share Post.
     */
    public String sharePost(int userId, int postId) {
        if (postDAO.sharePost(userId, postId)) {
            return "Post shared successfully to your feed!";
        } else {
            return "Error: Could not share post.";
        }
    }

    /**
     * Handles Dashboard Option 11: Deletes a post.
     */
    public String removePost(int postId, int userId) {
        if (postDAO.deletePost(postId, userId)) {
            return "Post ID " + postId + " deleted successfully.";
        } else {
            return "Error: Could not delete post.";
        }
    }
}