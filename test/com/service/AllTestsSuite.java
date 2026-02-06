package com.service;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import com.model.UserTest;

/**
 * Main Test Suite for RevConnect.
 * Groups the four core test classes to verify all 21 dashboard features.
 */
@Suite
@SelectClasses({
    UserTest.class,               // Verifies User Data Model
    AuthServiceTest.class,        // Verifies Profile, Bio, Logout, and Delete Account
    NetworkServiceTest.class,     // Verifies Suggestions, Following, Search, and Analytics
    PostServiceTest.class,        // Verifies Posting, Feed, Sharing, Hashtags, and Saved Posts
    InteractionServiceTest.class  // Verifies Likes, Comments, and Notifications
})
public class AllTestsSuite {
    // This class remains empty; it serves as a configuration for the JUnit runner.
}