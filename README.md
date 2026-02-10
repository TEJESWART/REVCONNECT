To align with your recent architectural upgrades, particularly the **Business Account integration**, **enhanced Post Type categorization**, and the **JUnit 5 Master Suite**, here is the revised and comprehensive **README.md**.

---

# 🌐 RevConnect – Console-Based Social Connectivity Engine

RevConnect is a robust, console-based social networking platform developed using **Java** and **MySQL**. It utilizes a complete **Model-View-Controller (MVC)** architecture to manage complex social interactions, data security, and specialized engagement analytics for Personal, Creator, and **Business** accounts.

---

## 🚀 Key Features

### 👤 User & Business Management

* **Secure Authentication**: Dedicated registration system supporting **Personal**, **Creator**, and **Business** account types.
* **Business Profiles**: Specialized profile fields for businesses, including Industry Category, Address, Website, and Operating Hours.
* **Profile Management**: Capabilities to update user bios and track personal engagement statistics.
* **Social Discovery**: Follow/unfollow system with built-in user suggestions and business discovery.

### 📝 Content & Interaction

* **Categorized Feed**: Real-time global feed with specialized visual markers (borders) for **Announcements** and **Promotions**.
* **Smart Content**: Features for sharing posts, hashtag parsing, and bookmarking content via "Save Post" functionality.
* **Data Security**: Strict **ownership-based deletion logic** ensures only creators can remove their own posts or comments.

### 📊 Advanced Analytics

* **Business Analytics**: Professional dashboard displaying total engagement, likes received, and follower growth trends.
* **Hashtag Engine**: Automatic parsing of `#hashtags` to track and discover trending topics.

### 🔔 Notifications

* **Real-time Alerts**: Instant notifications for post owners when content receives engagement (likes/comments).

---

## 🏛️ Project Architecture

The project follows a modular, layered approach to ensure separation of concerns:

* **Controller Layer (`com.controller`)**: The entry point managing dashboard logic and dynamic menus (e.g., Business vs. Personal dashboards).
* **Service Layer (`com.service`)**: The "Brain" of the application handling business logic, **account type validation**, and critical security/ownership checks.
* **DAO Layer (`com.dao`)**: Handles all database CRUD operations, including specialized result-set mapping for categorized posts.
* **Model Layer (`com.model`)**: Contains POJOs representing core entities (User, Post, Comment). Includes **ID aliasing** for service-layer compatibility.
* **Utility Layer (`com.util`)**: Manages JDBC connections and shared database utilities.

---

## 🛠️ Tech Stack

| Technology | Usage |
| --- | --- |
| **Java (JDK 17)** | Core Application Logic |
| **MySQL 8.0** | Relational Database Management |
| **JDBC** | Database Connectivity |
| **Apache Log4j2** | Professional Logging |
| **JUnit 5** | Automated Unit Testing & Test Suites |
| **Maven** | Dependency Management |

---

## 📂 Project Structure


```text
REVCONNECT
├── src/
│   ├── com.controller/
│   │   └── App.java                # Dynamic Dashboard & Account-Type Logic
│   ├── com.dao/                    # Database CRUD Operations
│   │   ├── InteractionDAO.java
│   │   ├── NetworkDAO.java
│   │   ├── PostDAO.java            # Handles PostType (Promotion/Announcement)
│   │   └── UserDAO.java
│   ├── com.model/                  # Data Entities (POJOs)
│   │   ├── Comment.java
│   │   ├── Likes.java
│   │   ├── Post.java
│   │   └── User.java               # Integrated with Business Fields
│   ├── com.service/                # Business & Security Logic
│   │   ├── AuthService.java
│   │   ├── InteractionService.java
│   │   ├── NetworkService.java
│   │   ├── NotificationService.java
│   │   └── PostService.java        # Feed Rendering & Business Validation
│   ├── com.util/
│   │   └── ConnectionFactory.java  # JDBC Connection Management
├── test/                           # Automated JUnit 5 Tests
│   ├── com.model/
│   │   └── UserTest.java           # Validates ID Aliasing
│   └── com.service/
│       ├── AllTestsSuite.java      # Master Suite for Project-Wide Validation
│       ├── AuthServiceTest.java
│       ├── InteractionServiceTest.java
│      
├── pom.xml                         # Maven Dependencies
└── schema.sql                      # Database Table Definitions

```

---

## ⚙️ Setup & Installation

### 1. Database Setup

1. Create a local MySQL database named `revconnect_db`.
2. Execute `schema.sql`.
3. **Important**: If updating from a previous version, run:
```sql
ALTER TABLE posts ADD COLUMN post_type VARCHAR(20) DEFAULT 'STANDARD';

```



### 2. Configure Connection

Update `src/com/util/ConnectionFactory.java` with your local **MySQL username** and **password**.

### 3. Execution

Run `App.java` to launch the console dashboard.

---

## 🧪 Testing

The project utilizes a **Master Test Suite** for comprehensive validation:

* **PostServiceTest**: Validates that only Business accounts can post promotions/announcements and ensures blank content is rejected.
* **AllTestsSuite**: One-click execution for all model and service tests.

---

## 📝 Future Scope

* **GUI Migration**: Transitioning to a JavaFX professional interface.
* **Enhanced Encryption**: Implementing password hashing (BCrypt).
* **Media Support**: Logic for handling image/video metadata in posts.

**Would you like me to help you finalize the `schema.sql` file to ensure all the new Business and PostType columns are included for a fresh install?**
