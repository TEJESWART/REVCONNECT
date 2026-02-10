package com.service;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

// Imports for the test classes
import com.model.UserTest;

import com.service.AuthServiceTest;
import com.service.InteractionServiceTest;

@Suite
@SelectClasses({
    UserTest.class,
    AuthServiceTest.class,
    InteractionServiceTest.class
})
public class AllTestsSuite {
    // This class remains empty; it serves as a configuration for the JUnit runner
}