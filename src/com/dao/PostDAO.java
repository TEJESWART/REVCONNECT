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
        String sql = "INSERT INTO posts (user_id, content, post_type) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, post.getUserId());
            pstmt.setString(2, post.getContent());
            pstmt.setString(3, post.getPostType() != null ? post.getPostType() : "STANDARD");
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("SQL Error in createPost: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Business Updates (Announcements/Promotions).
     */
    public boolean createBusinessPost(Post post) {
        return createPost(post);
    }

    /**
     * NEW: Global Feed - Fetches ALL posts from the database.
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
                post.setUserId(rs.getInt("user_id"));
                post.setUsername(rs.getString("username"));
                post.setContent(rs.getString("content"));
                post.setLikes(rs.getInt("total_likes"));
                post.setCreatedAt(rs.getTimestamp("created_at"));
                post.setPostType(rs.getString("post_type"));
                posts.add(post);
            }
        } catch (SQLException e) {
            logger.error("SQL Error in getAllPosts: {}", e.getMessage());
        }
        return posts;
    }

    /**
     * Option 2: Get unified feed (Followed users + Own posts).
     */
    public List<Post> getFollowedFeed(int currentUserId) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, u.username, (SELECT COUNT(*) FROM likes l WHERE l.post_id = p.post_id) as total_likes " +
                     "FROM posts p JOIN users u ON p.user_id = u.id " +
                     "LEFT JOIN follows f ON p.user_id = f.following_id AND f.follower_id = ? " +
                     "WHERE f.follower_id IS NOT NULL OR p.user_id = ? ORDER BY p.created_at DESC";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, currentUserId);
            pstmt.setInt(2, currentUserId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Post p = new Post();
                p.setPostId(rs.getInt("post_id"));
                p.setUserId(rs.getInt("user_id"));
                p.setContent(rs.getString("content"));
                p.setUsername(rs.getString("username"));
                p.setLikes(rs.getInt("total_likes"));
                p.setPostType(rs.getString("post_type"));
                posts.add(p);
            }
        } catch (SQLException e) {
            logger.error("Error fetching feed: {}", e.getMessage());
        }
        return posts;
    }

    /**
     * MY POSTS: Fetches only the user's posts.
     */
    public List<Post> getOnlyUserPosts(int userId) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, u.username, (SELECT COUNT(*) FROM likes l WHERE l.post_id = p.post_id) as total_likes " +
                     "FROM posts p JOIN users u ON p.user_id = u.id WHERE p.user_id = ? ORDER BY p.created_at DESC";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Post p = new Post();
                p.setPostId(rs.getInt("post_id"));
                p.setUserId(rs.getInt("user_id"));
                p.setContent(rs.getString("content"));
                p.setUsername(rs.getString("username"));
                p.setLikes(rs.getInt("total_likes"));
                p.setPostType(rs.getString("post_type"));
                posts.add(p);
            }
        } catch (SQLException e) {
            logger.error("Error fetching user posts: {}", e.getMessage());
        }
        return posts;
    }

    /**
     * TRENDING: Top 5 Posts.
     */
    public List<Post> getTrendingPosts() {
        List<Post> trending = new ArrayList<>();
        String sql = "SELECT p.*, u.username, COUNT(l.post_id) AS like_count " +
                     "FROM posts p JOIN users u ON p.user_id = u.id " +
                     "LEFT JOIN likes l ON p.post_id = l.post_id " +
                     "GROUP BY p.post_id ORDER BY like_count DESC LIMIT 5";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Post p = new Post();
                p.setPostId(rs.getInt("post_id"));
                p.setContent(rs.getString("content"));
                p.setUsername(rs.getString("username"));
                p.setLikes(rs.getInt("like_count"));
                p.setPostType(rs.getString("post_type"));
                trending.add(p);
            }
        } catch (SQLException e) {
            logger.error("Trending error: {}", e.getMessage());
        }
        return trending;
    }

    /**
     * Option 18: Trending Hashtags.
     * Parses all post content to find and count tags.
     */
    public void getTopHashtags() {
        String sql = "SELECT content FROM posts";
        Map<String, Integer> hashtagCounts = new HashMap<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String content = rs.getString("content");
                if (content == null) continue;

                String[] words = content.split("\\s+");
                for (String word : words) {
                    if (word.startsWith("#") && word.length() > 1) {
                        String tag = word.toLowerCase().replaceAll("[^#a-zA-Z0-9]", "");
                        if (!tag.isEmpty()) {
                            hashtagCounts.put(tag, hashtagCounts.getOrDefault(tag, 0) + 1);
                        }
                    }
                }
            }

            System.out.println("\n--- 🔥 TOP TRENDING HASHTAGS ---");
            if (hashtagCounts.isEmpty()) {
                System.out.println("No hashtags found yet. Start tagging your posts!");
            } else {
                hashtagCounts.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(5)
                    .forEach(entry -> System.out.println(entry.getKey() + " (" + entry.getValue() + " posts)"));
            }
            System.out.println("--------------------------------");

        } catch (SQLException e) {
            logger.error("SQL Error in getTopHashtags: {}", e.getMessage());
        }
    }

    /**
     * ANALYTICS: Returns user stats.
     */
    public void getUserAnalytics(int userId) {
        String sql = "SELECT " +
                     "(SELECT COUNT(*) FROM posts WHERE user_id = ?) AS total_posts, " +
                     "(SELECT COUNT(*) FROM likes l JOIN posts p ON l.post_id = p.post_id WHERE p.user_id = ?) AS likes_received";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("\n--- 📊 ANALYTICS ---");
                System.out.println("Total Posts: " + rs.getInt("total_posts"));
                System.out.println("Total Likes Received: " + rs.getInt("likes_received"));
            }
        } catch (SQLException e) {
            logger.error("Analytics error: {}", e.getMessage());
        }
    }

    /**
     * SHARE: Copy post content with "Shared:" prefix.
     */
    public boolean sharePost(int userId, int postId) {
        String sql = "INSERT INTO posts (user_id, content, post_type) SELECT ?, CONCAT('Shared: ', content), 'STANDARD' FROM posts WHERE post_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, postId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Share error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * HASHTAG SEARCH: Returns posts containing specific tag.
     */
    public List<Post> searchPostsByHashtag(String tag) {
        List<Post> results = new ArrayList<>();
        String sql = "SELECT p.*, u.username, (SELECT COUNT(*) FROM likes l WHERE l.post_id = p.post_id) as total_likes " +
                     "FROM posts p JOIN users u ON p.user_id = u.id WHERE p.content LIKE ? ORDER BY p.created_at DESC";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + tag + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Post post = new Post();
                post.setPostId(rs.getInt("post_id"));
                post.setUsername(rs.getString("username"));
                post.setContent(rs.getString("content"));
                post.setLikes(rs.getInt("total_likes"));
                post.setPostType(rs.getString("post_type"));
                results.add(post);
            }
        } catch (SQLException e) {
            logger.error("Hashtag search error: {}", e.getMessage());
        }
        return results;
    }

    public boolean postExists(int postId) {
        String sql = "SELECT 1 FROM posts WHERE post_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) { return false; }
    }

    public boolean deletePost(int postId, int userId) {
        String sql = "DELETE FROM posts WHERE post_id = ? AND user_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean savePost(int userId, int postId) {
        String sql = "INSERT IGNORE INTO saved_posts (user_id, post_id) VALUES (?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, postId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public List<Post> getSavedPosts(int userId) {
        List<Post> saved = new ArrayList<>();
        String sql = "SELECT p.*, u.username FROM posts p JOIN saved_posts s ON p.post_id = s.post_id JOIN users u ON p.user_id = u.id WHERE s.user_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Post post = new Post();
                post.setPostId(rs.getInt("post_id"));
                post.setUsername(rs.getString("username"));
                post.setContent(rs.getString("content"));
                saved.add(post);
            }
        } catch (SQLException e) { logger.error(e.getMessage()); }
        return saved;
    }

    public Post getTopUserPost(int userId) {
        String sql = "SELECT p.*, u.username, (SELECT COUNT(*) FROM likes l WHERE l.post_id = p.post_id) as like_count " +
                     "FROM posts p JOIN users u ON p.user_id = u.id " +
                     "WHERE p.user_id = ? ORDER BY like_count DESC LIMIT 1";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Post post = new Post();
                post.setPostId(rs.getInt("post_id"));
                post.setUsername(rs.getString("username"));
                post.setContent(rs.getString("content"));
                post.setLikes(rs.getInt("like_count"));
                post.setPostType(rs.getString("post_type"));
                return post;
            }
        } catch (SQLException e) {
            logger.error("Error in getTopUserPost: {}", e.getMessage());
        }
        return null; 
    }
}