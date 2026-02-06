package com.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class NetworkServiceTest {
    private NetworkService networkService = new NetworkService();

    @Test
    public void testFollowSelfConstraint() {
        // Verifies Option 5: Follow User
        String result = networkService.follow(101, 101);
        assertNotEquals("Followed!", result, "A user should not be able to follow themselves.");
    }

    @Test
    public void testUserSearchLogic() {
        // Verifies Option 6: Search
        assertDoesNotThrow(() -> networkService.findUsers("Ben", 1));
    }

    @Test
    public void testAnalyticsCounts() {
        // Verifies Option 19: My Analytics
        int followers = networkService.getFollowerCount(1);
        assertTrue(followers >= 0, "Analytics should return a non-negative follower count.");
    }
}