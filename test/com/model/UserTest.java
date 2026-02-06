package com.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class UserTest {

    @Test
    public void testUserBio() {
        // 1. Setup the user
        User user = new User();
        
        // 2. Set a bio
        String myBio = "Software Engineering Student";
        user.setBio(myBio);
        
        // 3. Check if the bio we "get" is the same one we "set"
        assertEquals(myBio, user.getBio(), "The bio should match what was set!");
    }
}