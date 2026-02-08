package com.dao;

import java.sql.*;
import java.util.*;
import com.model.Post;
import com.util.ConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PostDAO {
    private static final Logger logger = LogManager.getLogger(PostDAO.class);

    /**
     * Option 1: Create a new post.
     */
    public boolean createPost(Post post) {
        return createPostAndReturnId(post) > 0;
    }

    /**
     * HELPER FOR TESTS: Creates a post and returns the auto-generated ID.
     */
    public int createPostAndReturnId(Post post) {
        String sql = "INSERT INTO posts (user_id, content) VALUES (?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, post.getUserId());
            pstmt.setString(2, post.getContent());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); 
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("SQL Error in createPostAndReturnId: {}", e.getMessage());
        }
        return -1;
    }

    /**
     * HELPER: Checks if a post exists in the database.
     */
    public boolean postExists(int postId) {
        String sql = "SELECT 1 FROM posts WHERE post_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking post existence for ID {}: {}", postId, e.getMessage());
            return false;
        }
    }

    /**
     * Option 2: Get all posts for the global feed.
     */
    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, u.username, (SELECT COUNT(*) FROM likes l WHERE l.post_id = p.post_id) as total_likes " +
                     "FROM posts p JOIN users u ON p.user_id = u.id ORDER BY p.created_at DESC";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Post post = new Post();
                post.setPostId(rs.getInt("post_id"));
                post.setUsername(rs.getString("username"));
                post.setContent(rs.getString("content"));
                post.setLikes(rs.getInt("total_likes"));
                post.setCreatedAt(rs.getTimestamp("created_at"));
                posts.add(post);
            }
        } catch (SQLException e) {
            logger.error("SQL Error in getAllPosts: {}", e.getMessage());
        }
        return posts;
    }

    /**
     * Option 11: Delete a post (SECURE VERSION).
     * This version includes the user_id check in the SQL to enforce ownership.
     */
    public boolean deletePost(int postId, int userId) {
        // The AND user_id = ? ensures a user can ONLY delete their own posts
        String sql = "DELETE FROM posts WHERE post_id = ? AND user_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            pstmt.setInt(2, userId);
            
            // Returns true only if a row was actually found and deleted
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("SQL Error in deletePost for Post ID {}: {}", postId, e.getMessage());
            return false;
        }
    }

    /**
     * Option 16: Get Trending Top 5 Posts.
     */
    public List<Post> getTrendingPosts() {
        List<Post> trending = new ArrayList<>();
        String sql = "SELECT p.*, u.username, COUNT(l.post_id) AS like_count " +
                     "FROM posts p JOIN users u ON p.user_id = u.id " +
                     "LEFT JOIN likes l ON p.post_id = l.post_id " +
                     "GROUP BY p.post_id ORDER BY like_count DESC, p.created_at DESC LIMIT 5";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Post post = new Post();
                post.setPostId(rs.getInt("post_id"));
                post.setContent(rs.getString("content"));
                post.setUsername(rs.getString("username"));
                post.setLikes(rs.getInt("like_count")); 
                trending.add(post);
            }
        } catch (SQLException e) {
            logger.error("SQL Error in getTrendingPosts: {}", e.getMessage());
        }
        return trending;
    }

    /**
     * Option 17: Search Posts by Hashtag.
     */
    public List<Post> searchPostsByHashtag(String hashtag) {
        List<Post> results = new ArrayList<>();
        String sql = "SELECT p.*, u.username, (SELECT COUNT(*) FROM likes l WHERE l.post_id = p.post_id) as total_likes " +
                     "FROM posts p JOIN users u ON p.user_id = u.id " +
                     "WHERE p.content LIKE ? ORDER BY p.created_at DESC";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%#" + hashtag + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Post post = new Post();
                post.setPostId(rs.getInt("post_id"));
                post.setUsername(rs.getString("username"));
                post.setContent(rs.getString("content"));
                post.setLikes(rs.getInt("total_likes")); 
                post.setCreatedAt(rs.getTimestamp("created_at"));
                results.add(post);
            }
        } catch (SQLException e) {
            logger.error("SQL Error in searchPostsByHashtag: {}", e.getMessage());
        }
        return results;
    }

    /**
     * Option 18: Trending Hashtags.
     */
    public void getTopHashtags() {
        String sql = "SELECT content FROM posts";
        Map<String, Integer> hashtagCounts = new HashMap<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String content = rs.getString("content");
                String[] words = content.split("\\s+");
                for (String word : words) {
                    if (word.startsWith("#") && word.length() > 1) {
                        String tag = word.toLowerCase().replaceAll("[^#a-zA-Z0-9]", "");
                        hashtagCounts.put(tag, hashtagCounts.getOrDefault(tag, 0) + 1);
                    }
                }
            }

            System.out.println("\n--- 🔥 TOP TRENDING HASHTAGS ---");
            if (hashtagCounts.isEmpty()) {
                System.out.println("No hashtags found yet.");
            } else {
                hashtagCounts.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(5)
                    .forEach(entry -> System.out.println(entry.getKey() + " (" + entry.getValue() + " posts)"));
            }
        } catch (SQLException e) {
            logger.error("SQL Error in getTopHashtags: {}", e.getMessage());
        }
    }

    /**
     * Option 19 - User Analytics Dashboard.
     */
    public void getUserAnalytics(int userId) {
        String sql = "SELECT " +
                     "(SELECT COUNT(*) FROM posts WHERE user_id = ?) AS total_posts, " +
                     "(SELECT COUNT(*) FROM likes l JOIN posts p ON l.post_id = p.post_id WHERE p.user_id = ?) AS likes_received, " +
                     "(SELECT COUNT(*) FROM comments c JOIN posts p ON c.post_id = p.post_id WHERE p.user_id = ?) AS comments_received, " +
                     "(SELECT content FROM posts p LEFT JOIN likes l ON p.post_id = l.post_id " +
                     " WHERE p.user_id = ? GROUP BY p.post_id ORDER BY COUNT(l.post_id) DESC LIMIT 1) AS most_liked_post";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (int i = 1; i <= 4; i++) {
                pstmt.setInt(i, userId);
            }
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("\n--- 📊 MY ANALYTICS ---");
                System.out.println("Total Posts           : " + rs.getInt("total_posts"));
                System.out.println("Total Likes Received  : " + rs.getInt("likes_received"));
                System.out.println("Total Comments Rcvd   : " + rs.getInt("comments_received"));
                
                String bestPost = rs.getString("most_liked_post");
                System.out.println("Most Liked Post       : " + (bestPost != null ? "\"" + bestPost + "\"" : "N/A"));
            }
        } catch (SQLException e) { 
            logger.error("Analytics Error for User ID {}: {}", userId, e.getMessage());
        }
    }

    /**
     * Option 10: Share Post.
     */
    public boolean sharePost(int userId, int postId) {
        String sql = "INSERT INTO posts (user_id, content) SELECT ?, CONCAT('Shared: ', content) FROM posts WHERE post_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, postId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("SQL Error in sharePost: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Option 20 & 21: Bookmarks.
     */
    public boolean savePost(int userId, int postId) {
        String sql = "INSERT IGNORE INTO saved_posts (user_id, post_id) VALUES (?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, postId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("SQL Error in savePost: {}", e.getMessage());
            return false;
        }
    }

    public List<Post> getSavedPosts(int userId) {
        List<Post> saved = new ArrayList<>();
        String sql = "SELECT p.*, u.username, (SELECT COUNT(*) FROM likes l WHERE l.post_id = p.post_id) as total_likes " +
                     "FROM posts p JOIN saved_posts s ON p.post_id = s.post_id " +
                     "JOIN users u ON p.user_id = u.id WHERE s.user_id = ? ORDER BY s.saved_at DESC";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Post post = new Post();
                post.setPostId(rs.getInt("post_id"));
                post.setUsername(rs.getString("username"));
                post.setContent(rs.getString("content"));
                post.setLikes(rs.getInt("total_likes")); 
                post.setCreatedAt(rs.getTimestamp("created_at"));
                saved.add(post);
            }
        } catch (SQLException e) {
            logger.error("SQL Error in getSavedPosts: {}", e.getMessage());
        }
        return saved;
    }
}