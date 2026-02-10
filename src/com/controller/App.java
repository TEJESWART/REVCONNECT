package com.controller;

import java.util.Scanner;
import com.model.User;
import com.model.Post;
import com.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App {
    private static final Logger logger = LogManager.getLogger(App.class);
    private static Scanner sc = new Scanner(System.in);
    
    // Service initializations
    private static AuthService authService = new AuthService();
    private static PostService postService = new PostService();
    private static NetworkService networkService = new NetworkService();
    private static InteractionService interactionService = new InteractionService(); 
    private static NotificationService notificationService = new NotificationService();
    private static AdminService adminService = new AdminService();
    
    private static User loggedInUser = null;

    public static void main(String[] args) {
        logger.info("RevConnect Application Started.");
        System.out.println(">>> Starting RevConnect...");
        while (true) {
            if (loggedInUser == null) {
                showLoggedOutMenu();
            } else {
                if ("ADMIN".equalsIgnoreCase(loggedInUser.getUserType())) {
                    showAdminMenu();
                } else {
                    showLoggedInMenu();
                }
            }
        }
    }

    private static void showLoggedOutMenu() {
        System.out.println("\n--- WELCOME TO REVCONNECT ---");
        System.out.println("1. Register | 2. Login | 3. Exit");
        System.out.print("Choose: ");
        try {
            int choice = Integer.parseInt(sc.nextLine());
            switch(choice) {
                case 1: register(); break;
                case 2:
                    System.out.print("Username: "); String u = sc.nextLine();
                    System.out.print("Password: "); String p = sc.nextLine();
                    loggedInUser = authService.login(u, p);
                    
                    if (loggedInUser == null) {
                        System.out.println("Login Failed! Please check your credentials.");
                    } else {
                        System.out.println("\n====================================");
                        System.out.println("   WELCOME BACK, " + loggedInUser.getUsername().toUpperCase() + "!");
                        
                        Post top = postService.getTopPost(loggedInUser.getId());
                        if (top != null && top.getLikes() > 0) {
                            System.out.println("🔥 Your top post: \"" + top.getContent() + "\"");
                            System.out.println("✨ It has reached " + top.getLikes() + " likes!");
                        } else {
                            System.out.println("📢 Share something new today to start trending!");
                        }
                        System.out.println("====================================\n");
                    }
                    break;
                case 3: 
                    logger.info("User exited the application.");
                    System.out.println("Goodbye!");
                    System.exit(0);
            }
        } catch (Exception e) { 
            System.out.println("Invalid input. Please enter a number."); 
        }
    }

    private static void register() {
        System.out.println("\n--- 📝 CREATE AN ACCOUNT ---");
        System.out.print("Username: "); String name = sc.nextLine();
        System.out.print("Email: "); String email = sc.nextLine();
        System.out.print("Password: "); String pass = sc.nextLine();
        
        System.out.print("Account Type (PERSONAL / CREATOR / BUSINESS): "); 
        String type = sc.nextLine().toUpperCase();

        if (type.equals("BUSINESS")) {
            System.out.println("\n--- 🏢 BUSINESS PROFILE SETUP ---");
            System.out.print("Industry Category: "); String cat = sc.nextLine();
            System.out.print("Address: "); String addr = sc.nextLine();
            System.out.print("Website: "); String web = sc.nextLine();
            System.out.print("Hours: "); String hours = sc.nextLine();

            User newUser = new User(name, email, pass, type);
            String status = authService.registerBusiness(newUser, cat, addr, web, hours);
            System.out.println("Status: " + status);
        } else if (type.equals("PERSONAL") || type.equals("CREATOR")) {
            User newUser = new User(name, email, pass, type);
            String status = authService.register(newUser);
            System.out.println("Status: " + status);
        } else {
            System.out.println("⚠️ Invalid Type.");
        }
    }

    private static void showAdminMenu() {
        System.out.println("\n--- 🛠️ ADMIN CONTROL PANEL ---");
        System.out.println("1. Global Analytics | 2. Moderate Content | 3. Logout");
        System.out.print("Choose: ");
        try {
            int choice = Integer.parseInt(sc.nextLine());
            switch (choice) {
                case 1: adminService.showPlatformStats(); break;
                case 2: 
                    System.out.print("Enter Post ID to remove: ");
                    adminService.moderateContent(Integer.parseInt(sc.nextLine()));
                    break;
                case 3: loggedInUser = null; break;
            }
        } catch (Exception e) { System.out.println("Admin error."); }
    }

    private static void showLoggedInMenu() {
        int unread = notificationService.getUnreadCount(loggedInUser.getId());
        String alert = (unread > 0) ? " [" + unread + " NEW!]" : "";

        System.out.println("\n--- DASHBOARD (" + loggedInUser.getUsername().toUpperCase() + ")" + alert + " ---");
        System.out.println("1. Post         | 2. My Posts   | 3. Suggestions | 4. Profile");
        System.out.println("5. Follow User  | 6. Search     | 7. Edit Bio    | 8. Like");
        System.out.println("9. Comment      | 10. Share     | 11. Delete Post| 12. Logout");
        System.out.println("13. DELETE ACCT | 14. REMOVE COMMENT | 15. NOTIFICATIONS"); 
        System.out.println("16. TRENDING    | 17. HASHTAG SEARCH | 18. TOP TAGS"); 
        System.out.println("19. MY ANALYTICS| 20. SAVE POST      | 21. VIEW SAVED");
        System.out.println("22. UNLIKE POST | 23. VIEW COMMENTS");

        // DYNAMIC BUSINESS MENU
        if ("BUSINESS".equalsIgnoreCase(loggedInUser.getUserType())) {
            System.out.println("-------------------------------------------------------------------");
            System.out.println("B1. Post Announcement | B2. Post Promotion");
        }
        
        System.out.print("Choose: ");
        String choiceStr = sc.nextLine().toUpperCase();
        
        try {
            // Handle Business String Choices
            if (choiceStr.equals("B1")) {
                System.out.print("Announcement Content: ");
                System.out.println(postService.postBusinessUpdate(loggedInUser, sc.nextLine(), "ANNOUNCEMENT"));
                return;
            } else if (choiceStr.equals("B2")) {
                System.out.print("Promotional Content: ");
                System.out.println(postService.postBusinessUpdate(loggedInUser, sc.nextLine(), "PROMOTION"));
                return;
            }

            // Handle Standard Numeric Choices
            int choice = Integer.parseInt(choiceStr);
            switch (choice) {
                case 1: 
                    System.out.print("Post content: "); 
                    System.out.println(postService.addNewPost(loggedInUser.getId(), sc.nextLine())); 
                    break;
                case 2: 
                    postService.displayUserOnlyFeed(loggedInUser.getId()); 
                    break;
                case 3: networkService.showSuggestions(loggedInUser.getId(), loggedInUser.getUserType()); break;
                case 4: 
                    User p = authService.getProfile(loggedInUser.getId());
                    System.out.println("\n--- 👤 PROFILE: " + p.getUsername().toUpperCase() + " ---");
                    System.out.println("Account Type: " + p.getUserType());
                    System.out.println("Bio:          " + (p.getBio() != null ? p.getBio() : "No bio."));
                    
                    if ("BUSINESS".equalsIgnoreCase(p.getUserType())) {
                        System.out.println("\n--- 🏢 BUSINESS INFORMATION ---");
                        System.out.println("Industry:     " + (p.getCategory() != null ? p.getCategory() : "N/A"));
                        System.out.println("Address:      " + (p.getAddress() != null ? p.getAddress() : "N/A"));
                        System.out.println("Website:      " + (p.getWebsite() != null ? p.getWebsite() : "N/A"));
                        System.out.println("Hours:        " + (p.getHours() != null ? p.getHours() : "N/A"));
                    }

                    System.out.println("\n--- 📈 STATS ---");
                    System.out.println("Followers:    " + networkService.getFollowerCount(p.getId()));
                    System.out.println("Following:    " + networkService.getFollowingCount(p.getId()));
                    break;
                case 5:
                    System.out.print("Follow User ID: ");
                    System.out.println(networkService.follow(loggedInUser.getId(), Integer.parseInt(sc.nextLine())));
                    break;
                case 6:
                    System.out.print("Search: ");
                    networkService.findUsers(sc.nextLine(), loggedInUser.getId());
                    break;
                case 7: 
                    System.out.print("New Bio: ");
                    System.out.println(authService.updateProfileBio(loggedInUser.getId(), sc.nextLine()));
                    break;
                case 8:
                    System.out.print("Like Post ID: ");
                    System.out.println(interactionService.likePost(loggedInUser.getId(), Integer.parseInt(sc.nextLine())));
                    break;
                case 9:
                    System.out.print("Comment Post ID: "); int pid = Integer.parseInt(sc.nextLine());
                    System.out.print("Comment: ");
                    System.out.println(interactionService.commentOnPost(loggedInUser.getId(), pid, sc.nextLine()));
                    break;
                case 10:
                    System.out.print("Share Post ID: ");
                    System.out.println(postService.sharePost(loggedInUser.getId(), Integer.parseInt(sc.nextLine())));
                    break;
                case 11:
                    System.out.print("Delete Post ID: ");
                    System.out.println(postService.deletePost(Integer.parseInt(sc.nextLine()), loggedInUser.getId()));
                    break;
                case 12: loggedInUser = null; break;
                case 13:
                    System.out.print("Type 'CONFIRM' to delete: ");
                    if (sc.nextLine().equalsIgnoreCase("CONFIRM")) {
                        authService.deleteAccount(loggedInUser.getId());
                        loggedInUser = null;
                    }
                    break;
                case 14:
                    System.out.print("Remove Comment ID: ");
                    System.out.println(interactionService.removeComment(Integer.parseInt(sc.nextLine()), loggedInUser.getId()));
                    break;
                case 15: notificationService.showNotifications(loggedInUser.getId()); break;
                case 16: postService.showTrending(); break;
                case 17:
                    System.out.print("Search Hashtag: ");
                    postService.discoverHashtags(sc.nextLine());
                    break;
                case 18: postService.showTrendingHashtags(); break;
                case 19: 
                    int followersCount = networkService.getFollowerCount(loggedInUser.getId()); 
                    int followingCount = networkService.getFollowingCount(loggedInUser.getId()); 
                    postService.showUserAnalytics(loggedInUser.getId(), followersCount, followingCount); 
                    break;
                case 20: 
                    System.out.print("Save Post ID: ");
                    System.out.println(postService.bookmarkPost(loggedInUser.getId(), Integer.parseInt(sc.nextLine())));
                    break;
                case 21: postService.displaySavedPosts(loggedInUser.getId()); break;
                case 22:
                    System.out.print("Enter Post ID to unlike: ");
                    int unlikeId = Integer.parseInt(sc.nextLine());
                    System.out.println(interactionService.unlikePost(loggedInUser.getId(), unlikeId));
                    break;
                case 23:
                    System.out.print("Enter Post ID to see all comments: ");
                    int commentPostId = Integer.parseInt(sc.nextLine());
                    interactionService.displayAllComments(commentPostId);
                    break;
                default: System.out.println("Invalid Choice.");
            }
        } catch (Exception e) { 
            System.out.println("Input Error. Please enter a valid menu option."); 
        }
    }
}