# RevConnect 🌐
**A Secure, Scalable Social Media Backend Engine**

RevConnect is a robust console-based social networking platform built using **Java** and **MySQL**. It implements a complete Model-View-Controller (MVC) architecture to handle complex social interactions, data security, and engagement analytics.

---

## 🚀 Key Features

### 👤 User Management
* **Secure Authentication:** Login and Registration system.
* **Profile Management:** Update bio and view personal engagement stats.
* **Social Discovery:** Follow/Unfollow system with user suggestions.

### 📝 Content & Interaction
* **Dynamic Feed:** Real-time global feed with nested comments and like counts.
* **Smart Content:** Post sharing and bookmarking (Save Post) features.
* **Data Security:** Strict ownership-based deletion logic for posts and comments.

### 📊 Advanced Analytics
* **Hashtag Engine:** Automatic parsing of `#hashtags` to track trending topics.
* **User Analytics:** Dashboard showing total engagement, likes received, and most-liked posts.

### 🔔 Notifications
* **Real-time Alerts:** Notifies post owners when their content receives likes or comments.

---

## 🛠️ Tech Stack
* **Language:** Java (JDK 17+)
* **Database:** MySQL 8.0
* **Driver:** JDBC (MySQL Connector/J)
* **Logging:** Apache Log4j2
* **Testing:** JUnit 5

---

## 📂 Project Structure



text
src/com/
├── dao/           # Data Access Objects (SQL Queries)
├── service/       # Business Logic & Security Checks
├── model/         # Plain Old Java Objects (User, Post, Comment)
├── util/          # Connection Factory & Database Utilities
└── App.java       # Main Controller & Dashboard UI
⚙️ Setup & Installation
Clone the repository:

Bash
git clone [https://github.com/yourusername/RevConnect.git](https://github.com/yourusername/RevConnect.git)
Database Setup:

Create a database named revconnect_db.

Execute the provided schema.sql file to create tables (users, posts, comments, likes, follows, notifications, saved_posts).

Configure Connection:

Update src/com/util/ConnectionFactory.java with your MySQL username and password.

Run the Application:

Compile and run App.java.

🧪 Testing
The project includes a comprehensive JUnit 5 test suite to ensure the integrity of social interactions.

InteractionServiceTest: Validates liking, commenting, and ownership-based deletion.

📝 Future Scope
GUI Migration: Moving from console-based to a JavaFX/Swing interface.

Encryption: Implementing BCrypt for password hashing.

Search: Advanced full-text search for posts and users.
