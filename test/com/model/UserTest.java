package com.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

// If this line stays red after the Maven update, 
// the test folder isn't seeing the src folder.
import com.model.User; 

public class UserTest {

    @Test
    public void testUserProperties() {
        User user = new User();
        user.setUsername("test_user");
        user.setBio("Software Engineer");
        user.setUserType("BUSINESS");

        assertAll("Verify User Model State",
            () -> assertEquals("test_user", user.getUsername()),
            () -> assertEquals("Software Engineer", user.getBio()),
            () -> assertEquals("BUSINESS", user.getUserType())
        );
    }
}