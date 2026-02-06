package com.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.model.User;

public class AuthServiceTest {
    private AuthService authService = new AuthService();

    @Test
    public void testLoginLogic() {
        // Verifies Option 2: Login
        User user = authService.login("admin", "admin123");
        assertNotNull(user, "Login should return a valid User object for correct credentials.");
    }

    @Test
    public void testProfileRetrieval() { 
        // Verifies Option 4: Profile
        User user = authService.getProfile(1);
        assertNotNull(user, "Should be able to fetch profile details by ID.");
    }

    @Test
    public void testBioUpdateLogic() {
        // Verifies Option 7: Edit Bio
        String result = authService.updateProfileBio(1, "Updated JUnit Bio");
        assertEquals("Bio Updated!", result);
    }

    @Test
    public void testDeleteAccountFailure() {
        // Verifies Option 13: Delete Account
        // Testing with a non-existent ID to ensure error handling works
        String result = authService.deleteAccount(9999);
        assertEquals("Error.", result, "Should return Error for non-existent user IDs.");
    }
}