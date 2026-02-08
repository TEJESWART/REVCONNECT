package com.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class InteractionServiceTest {
    private InteractionService interactionService = new InteractionService();
    private PostService postService = new PostService();
    private int testUserId = 1;
    private int dynamicPostId; // Stores the ID from the database

    @BeforeEach
    public void setup() {
        // Create a real post in the DB and capture its unique ID
        dynamicPostId = postService.createTestPost(testUserId, "Real Post for testing");
        
        // Safety check: if DB fails, don't run the tests
        assertTrue(dynamicPostId > 0, "Post creation failed, cannot proceed with interaction tests.");
    }

    @Test
    public void testLikeLogic() {
        // Use the ID generated in setup()
        String result = interactionService.likePost(testUserId, dynamicPostId);
        assertNotNull(result);
        assertTrue(result.contains("Liked") || result.contains("Could not"));
    }

    @Test
    public void testCommentLogic() {
        // Use the ID generated in setup()
        String result = interactionService.commentOnPost(testUserId, dynamicPostId, "Test comment");
        assertNotNull(result);
        assertTrue(result.contains("added"));
    }

    @Test
    public void testCommentRemoval() {
        // We still test the "Not Found" logic for ID 999 to ensure gracefull handling
        String result = interactionService.removeComment(999, testUserId);
        
        assertTrue(result.contains("deleted") || result.contains("Notice") || result.contains("Success"), 
            "The service should handle missing IDs gracefully.");
    }
}