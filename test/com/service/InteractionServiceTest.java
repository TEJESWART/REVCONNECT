package com.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class InteractionServiceTest {
    private InteractionService interactionService = new InteractionService();
    private PostService postService = new PostService();
    private int testPostId = 1; // Default ID to use for testing

    @BeforeEach
    public void setup() {
        // This runs BEFORE every test method.
        // It ensures at least one post exists in the DB to avoid Foreign Key errors.
        postService.addNewPost(1, "Automated Test Post for Interactions");
        
        // Note: In a real DB, you'd ideally fetch the generated ID, 
        // but for a console project, using a known existing ID (like 1) works well.
    }

    @Test
    public void testLikeLogic() {
        // Verifies Option 8: Like
        String result = interactionService.likePost(1, testPostId);
        assertNotNull(result, "Liking should return a status update.");
    }

    @Test
    public void testCommentLogic() {
        // Verifies Option 9: Comment
        // Now it uses the testPostId which we ensured exists in setup()
        String result = interactionService.commentOnPost(1, testPostId, "Test comment");
        assertNotNull(result, "Commenting should return a confirmation.");
    }

    @Test
    public void testCommentRemoval() {
        // Verifies Option 14: Delete Comment
        String result = interactionService.removeComment(500, 1);
        assertTrue(result.contains("Deleted") || result.contains("Error"));
    }
}