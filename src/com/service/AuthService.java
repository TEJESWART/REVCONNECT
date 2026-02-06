package com.service;

import com.dao.UserDAO;
import com.model.User;

public class AuthService {
    private UserDAO userDAO = new UserDAO();

    public User login(String username, String password) {
        return userDAO.login(username, password);
    }

    public String register(User user) {
        return userDAO.registerUser(user) ? "Success" : "Error";
    }

    public String updateProfileBio(int userId, String bio) {
        return userDAO.updateBio(userId, bio) ? "Bio Updated!" : "Update Failed.";
    }

    public User getProfile(int userId) {
        return userDAO.getUserById(userId);
    }

    public String deleteAccount(int userId) {
        return userDAO.deleteUser(userId) ? "Account Deleted Successfully." : "Error.";
    }
}