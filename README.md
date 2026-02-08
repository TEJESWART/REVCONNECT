
# 🌐 RevConnect – Console-Based Social Connectivity Engine

RevConnect is a robust, console-based social networking platform developed using **Java** and **MySQL**. It utilizes a complete **Model-View-Controller (MVC)** architecture to manage complex social interactions, data security, and engagement analytics. The platform is designed to be secure and scalable, providing a professional foundation for social networking features.

---

## 🚀 Key Features

### 👤 User Management

* **Secure Authentication**: Dedicated registration and login system.
* **Profile Management**: Capabilities to update user bios and track personal engagement statistics.
* **Social Discovery**: Follow and unfollow system with built-in user suggestions.

### 📝 Content & Interaction

* **Dynamic Feed**: Real-time global feed displaying nested comments and live like counts.
* **Smart Content**: Features for sharing posts and bookmarking content via "Save Post" functionality.
* **Data Security**: Strict **ownership-based deletion logic** ensures only creators can remove their own posts or comments.

### 📊 Advanced Analytics

* **Hashtag Engine**: Automatic parsing of `#hashtags` to track and discover trending topics.
* **User Analytics**: Professional dashboard displaying total engagement, likes received, and top-performing posts.

### 🔔 Notifications

* **Real-time Alerts**: Instant notifications for post owners when their content receives engagement such as likes or comments.

---

## 🏛️ Project Architecture

The project follows a modular, layered approach to ensure separation of concerns and maintainability:

* **Controller Layer (`com.controller`)**: The entry point managing dashboard logic and user menus.
* **Service Layer (`com.service`)**: The "Brain" of the application handling business logic and critical security/ownership checks.
* **DAO Layer (`com.dao`)**: Handles all database CRUD operations and persistent storage logic.
* **Model Layer (`com.model`)**: Contains Plain Old Java Objects (POJOs) representing the core entities like User, Post, and Comment.
* **Utility Layer (`com.util`)**: Manages JDBC connections and shared database utilities.

---

## 🛠️ Tech Stack

| Technology | Usage |
| --- | --- |
| **Java (JDK 17)** | Core Application Logic |
| **MySQL 8.0** | Relational Database Management |
| **JDBC** | Database Connectivity |
| **Apache Log4j2** | Professional Logging |
| **JUnit 5** | Automated Unit Testing |
| **Maven** | Dependency Management |

---

## 📂 Project Structure


REVCONNECT
├── src/
│   ├── com.controller/
│   │   └── App.java                # Main Dashboard & Menu Logic
│   ├── com.dao/                    # Database CRUD Operations
│   │   ├── InteractionDAO.java
│   │   ├── NetworkDAO.java
│   │   ├── PostDAO.java
│   │   └── UserDAO.java
│   ├── com.model/                  # Data Entities (POJOs)
│   │   ├── Comment.java
│   │   ├── Likes.java
│   │   ├── Post.java
│   │   └── User.java
│   ├── com.service/                # Business & Security Logic
│   │   ├── AuthService.java
│   │   ├── InteractionService.java
│   │   ├── NetworkService.java
│   │   ├── NotificationService.java
│   │   └── PostService.java
│   ├── com.util/
│   │   └── ConnectionFactory.java  # JDBC Connection Management
│   └── log4j2.xml                  # Logging Configuration
├── test/                           # Automated JUnit 5 Tests
│   ├── com.model/
│   │   └── UserTest.java
│   └── com.service/
│       ├── AllTestsSuite.java
│       ├── AuthServiceTest.java
│       ├── InteractionServiceTest.java
│       └── NetworkServiceTest.java
├── Docs/                           # Project Documentation
│   ├── Architecture.md
│   ├── ERD.md
│   └── ERD_PUML.md
├── pom.xml                         # Maven Dependencies
├── README.md                       # Project Overview
└── schema.sql                      # Database Table Definitions


## ⚙️ Setup & Installation

### 1. Clone the Repository

git clone [https://github.com/yourusername/RevConnect.git]
(https://github.com/yourusername/RevConnect.git)




### 2. Database Setup

1. Create a local MySQL database named `revconnect_db`.
2. Execute the provided `schema.sql` file to generate the following tables: `users`, `posts`, `comments`, `likes`, `follows`, `notifications`, and `saved_posts`.

### 3. Configure Connection

* Open `src/com/util/ConnectionFactory.java`.
* Update the file with your local **MySQL username** and **password**.

### 4. Run the Application

* Compile the project using Maven.
* Run `App.java` to launch the console dashboard.

---

## 🧪 Testing

The project includes a comprehensive test suite to ensure system integrity:

* **InteractionServiceTest**: Validates core social behaviors like liking, commenting, and the crucial **ownership-based deletion** security.
* **AuthServiceTest**: Ensures secure login and session management.

---

## 📝 Future Scope

* **GUI Migration**: Moving from a console-based interface to a modern JavaFX or Swing GUI.
* **Enhanced Encryption**: Implementing industry-standard password hashing.
* **Advanced Search**: Adding full-text search capabilities for posts and user discovery.

