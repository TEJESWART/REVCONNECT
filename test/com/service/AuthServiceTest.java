package com.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.model.User;

public class AuthServiceTest {
    private AuthService authService = new AuthService();

    @Test
    public void testInvalidLoginReturnsNull() {
        // Testing that the service correctly handles failed authentication
        User result = authService.login("non_existent_user", "wrong_password");
        assertNull(result, "Login should return null for invalid credentials");
    }
}