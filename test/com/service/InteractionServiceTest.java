package com.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class InteractionServiceTest {
    private InteractionService interactionService = new InteractionService();

    @Test
    public void testLikePostLogic() {
        // Ensuring the service handles the request without crashing
        String result = interactionService.likePost(1, 10);
        assertNotNull(result, "Like action should return a status string.");
    }

    @Test
    public void testCommentValidation() {
        // Test that empty comments are caught by the service validation
        String emptyResult = interactionService.commentOnPost(1, 10, "");
        assertTrue(emptyResult.contains("empty") || emptyResult.contains("Invalid") || emptyResult.contains("❌"), 
            "Blank comments should be rejected.");
    }
}