package com.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class PostServiceTest {
    private PostService postService = new PostService();

    @Test
    public void testEmptyPostPrevention() {
        // Verifies Option 1: Post
        String result = postService.addNewPost(1, "");
        assertNotEquals("Successfully Published!", result, "The system should block empty posts.");
    }

    @Test
    public void testSharePostLogic() {
        // Verifies Option 10: Share
        String result = postService.sharePost(1, 101);
        assertNotNull(result, "Share service should return a status string.");
    }

    @Test
    public void testHashtagSearchExecution() {
        // Verifies Option 17: Hashtag Search
        assertDoesNotThrow(() -> postService.discoverHashtags("Java"), 
            "Searching for a hashtag should not crash the system.");
    }

    @Test
    public void testBookmarkLogic() {
        // Verifies Option 20: Save Post
        String result = postService.bookmarkPost(1, 105);
        assertNotNull(result, "Service should return a confirmation message.");
    }
}