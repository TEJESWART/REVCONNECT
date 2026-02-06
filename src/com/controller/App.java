package com.controller;

import java.util.Scanner;
import com.model.User;
import com.service.*;

public class App {
    private static Scanner sc = new Scanner(System.in);
    private static AuthService authService = new AuthService();
    private static PostService postService = new PostService();
    private static NetworkService networkService = new NetworkService();
    private static InteractionService interactionService = new InteractionService(); 
    private static NotificationService notificationService = new NotificationService();
    private static User loggedInUser = null;

    public static void main(String[] args) {
        System.out.println(">>> Starting RevConnect...");
        while (true) {
            if (loggedInUser == null) showLoggedOutMenu();
            else showLoggedInMenu();
        }
    }

    private static void showLoggedOutMenu() {
        System.out.println("\n--- WELCOME TO REVCONNECT ---");
        System.out.println("1. Register | 2. Login | 3. Exit");
        System.out.print("Choose: ");
        try {
            int choice = Integer.parseInt(sc.nextLine());
            switch(choice) {
                case 1:
                    System.out.print("Username: "); String name = sc.nextLine();
                    System.out.print("Email: "); String email = sc.nextLine();
                    System.out.print("Password: "); String pass = sc.nextLine();
                    System.out.print("Account Type (PERSONAL/CREATOR): "); String type = sc.nextLine();
                    
                    User newUser = new User(name, email, pass, type.toUpperCase());
                    String status = authService.register(newUser);
                    if (status.equalsIgnoreCase("Success")) {
                        System.out.println("Registration Successful! Please login.");
                    } else {
                        System.out.println("Registration failed: " + status);
                    }
                    break;
                case 2:
                    System.out.print("Username: "); String u = sc.nextLine();
                    System.out.print("Password: "); String p = sc.nextLine();
                    loggedInUser = authService.login(u, p);
                    if (loggedInUser == null) {
                        System.out.println("Login Failed! Please check your credentials.");
                    } else {
                        System.out.println("Login Successful! Welcome, " + loggedInUser.getUsername());
                    }
                    break;
                case 3: 
                    System.out.println("Exiting RevConnect... Goodbye!");
                    System.exit(0);
            }
        } catch (Exception e) { 
            System.out.println("Invalid input. Please enter a number."); 
        }
    }

    private static void showLoggedInMenu() {
        // --- FETCH UNREAD NOTIFICATIONS FOR ALERT ---
        int unread = notificationService.getUnreadCount(loggedInUser.getId());
        String alert = (unread > 0) ? " [" + unread + " NEW!]" : "";

        System.out.println("\n--- DASHBOARD (" + loggedInUser.getUsername().toUpperCase() + ")" + alert + " ---");
        System.out.println("1. Post         | 2. Feed       | 3. Suggestions | 4. Profile");
        System.out.println("5. Follow User  | 6. Search     | 7. Edit Bio    | 8. Like");
        System.out.println("9. Comment      | 10. Share     | 11. Delete Post| 12. Logout");
        System.out.println("13. DELETE ACCT | 14. DELETE COMMENT | 15. NOTIFICATIONS"); 
        System.out.println("16. TRENDING FEED | 17. HASHTAG SEARCH | 18. TRENDING TAGS"); 
        System.out.println("19. MY ANALYTICS  | 20. SAVE POST      | 21. VIEW SAVED");
        System.out.print("Choose: ");
        
        try {
            int choice = Integer.parseInt(sc.nextLine());
            switch (choice) {
                case 1: 
                    System.out.print("What's on your mind? : "); 
                    System.out.println(postService.addNewPost(loggedInUser.getId(), sc.nextLine())); 
                    break;
                case 2: 
                    postService.displayFeed(); 
                    break;
                case 3: 
                    networkService.showSuggestions(loggedInUser.getId(), loggedInUser.getUserType()); 
                    break;
                case 4: 
                    User profileData = authService.getProfile(loggedInUser.getId());
                    System.out.println("\n--- MY PROFILE ---");
                    System.out.println("Username:  " + profileData.getUsername());
                    System.out.println("Bio:       " + (profileData.getBio() != null ? profileData.getBio() : "No bio set yet."));
                    System.out.println("Followers: " + networkService.getFollowerCount(profileData.getId()));
                    System.out.println("Following: " + networkService.getFollowingCount(profileData.getId()));
                    System.out.println("Type:      " + profileData.getUserType());
                    break;
                case 5:
                    System.out.print("Enter User ID to follow: ");
                    int followId = Integer.parseInt(sc.nextLine());
                    System.out.println(networkService.follow(loggedInUser.getId(), followId));
                    break;
                case 6:
                    System.out.print("Search username: ");
                    networkService.findUsers(sc.nextLine(), loggedInUser.getId());
                    break;
                case 7: 
                    System.out.print("Enter new bio: ");
                    System.out.println(authService.updateProfileBio(loggedInUser.getId(), sc.nextLine()));
                    break;
                case 8:
                    System.out.print("Post ID to Like: ");
                    int likePostId = Integer.parseInt(sc.nextLine());
                    System.out.println(interactionService.likePost(loggedInUser.getId(), likePostId));
                    break;
                case 9:
                    System.out.print("Post ID to comment on: "); 
                    int pId = Integer.parseInt(sc.nextLine());
                    System.out.print("Enter Comment: "); 
                    String commentText = sc.nextLine();
                    System.out.println(interactionService.commentOnPost(loggedInUser.getId(), pId, commentText));
                    break;
                case 10:
                    System.out.print("Post ID to Share: ");
                    System.out.println(postService.sharePost(loggedInUser.getId(), Integer.parseInt(sc.nextLine())));
                    break;
                case 11:
                    System.out.print("Post ID to Delete: ");
                    System.out.println(postService.removePost(Integer.parseInt(sc.nextLine()), loggedInUser.getId()));
                    break;
                case 12: 
                    loggedInUser = null; 
                    System.out.println("Logged out successfully.");
                    break;
                case 13:
                    System.out.print("Type 'CONFIRM' to delete account: ");
                    if (sc.nextLine().equalsIgnoreCase("CONFIRM")) {
                        System.out.println(authService.deleteAccount(loggedInUser.getId()));
                        loggedInUser = null;
                    } else {
                        System.out.println("Account deletion cancelled.");
                    }
                    break;
                case 14: 
                    System.out.print("Enter Comment ID to delete: ");
                    int commentId = Integer.parseInt(sc.nextLine());
                    System.out.println(interactionService.removeComment(commentId, loggedInUser.getId()));
                    break;
                case 15: 
                    notificationService.showNotifications(loggedInUser.getId());
                    break;
                case 16: 
                    postService.showTrending();
                    break;
                case 17:
                    System.out.print("Enter hashtag to search: ");
                    postService.discoverHashtags(sc.nextLine());
                    break;
                case 18: 
                    postService.showTrendingHashtags();
                    break;
                case 19: 
                    int fers = networkService.getFollowerCount(loggedInUser.getId());
                    int fing = networkService.getFollowingCount(loggedInUser.getId());
                    postService.showUserAnalytics(loggedInUser.getId(), fers, fing);
                    break;
                case 20: 
                    System.out.print("Enter Post ID to save: ");
                    int saveId = Integer.parseInt(sc.nextLine());
                    System.out.println(postService.bookmarkPost(loggedInUser.getId(), saveId));
                    break;
                case 21: 
                    postService.displaySavedPosts(loggedInUser.getId());
                    break;
                 
                default:
                    System.out.println("Selection out of range (1-21).");
            }
        } catch (NumberFormatException e) { 
            System.out.println("Input Error: Please enter a valid numerical choice."); 
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }
}