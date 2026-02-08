-- Step 1: Create the Database
CREATE DATABASE IF NOT EXISTS revconnect_db;
USE revconnect_db;

-- Step 2: Create the Users table
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL,
    user_type ENUM('PERSONAL', 'BUSINESS', 'CREATOR') NOT NULL,
    bio TEXT
);

-- Step 3: Create the Posts table
CREATE TABLE posts (
    post_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    content TEXT NOT NULL,
    likes INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Table to track which user liked which post
CREATE TABLE post_likes (
    like_id INT PRIMARY KEY AUTO_INCREMENT,
    post_id INT,
    user_id INT,
    UNIQUE(post_id, user_id), -- Prevents duplicate likes
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Table for comments
CREATE TABLE comments (
    comment_id INT PRIMARY KEY AUTO_INCREMENT,
    post_id INT,
    user_id INT,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);


USE revconnect_db;

-- Table to track the social graph (Followers/Following)
CREATE TABLE IF NOT EXISTS follows (
    follower_id INT NOT NULL,
    following_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (follower_id, following_id),
    FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE likes (
    user_id INT,
    post_id INT,
    PRIMARY KEY (user_id, post_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE
);

CREATE TABLE comments (
    comment_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    post_id INT,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE
);

USE revconnect_db;
ALTER TABLE users ADD COLUMN IF NOT EXISTS last_login TIMESTAMP NULL;



USE revconnect_db;
SELECT id, username FROM users;


ALTER TABLE users ADD COLUMN IF NOT EXISTS bio TEXT;
	USE revconnect_db;
	SELECT * FROM users;
    
SELECT * FROM users 
WHERE user_id != ? 
AND user_id NOT IN (
    SELECT receiver_id FROM connections WHERE sender_id = ?
    UNION
    SELECT sender_id FROM connections WHERE receiver_id = ?
)
LIMIT 5;
    CREATE TABLE IF NOT EXISTS follows (
    follower_id INT,
    following_id INT,
    PRIMARY KEY (follower_id, following_id),
    FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS likes (
    user_id INT,
    post_id INT,
    PRIMARY KEY (user_id, post_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments (
    comment_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    post_id INT,
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE
);

SELECT * FROM follows WHERE follower_id = 1 AND following_id = 2;



-- Table for Likes (Prevents duplicate likes from same user)
CREATE TABLE IF NOT EXISTS likes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    UNIQUE(user_id, post_id), -- This prevents "Already liked" errors
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

-- Table for Comments
CREATE TABLE IF NOT EXISTS comments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS posts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Adding Creator accounts for Ben to discover
INSERT INTO users (username, email, password, user_type) VALUES 
('TechVlog', 'tech@rev.com', 'pass123', 'CREATOR'),
('CookingKing', 'chef@rev.com', 'pass123', 'CREATOR'),
('TravelBug', 'travel@rev.com', 'pass123', 'CREATOR');


-- Adding a column to track which post is being shared
ALTER TABLE posts ADD COLUMN parent_post_id INT DEFAULT NULL;
ALTER TABLE posts ADD FOREIGN KEY (parent_post_id) REFERENCES posts(id) ON DELETE SET NULL;

DESCRIBE comments;

-- notififcations

CREATE TABLE notifications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,        -- The person receiving the notification
    message TEXT NOT NULL,       -- e.g., "Ben liked your post"
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

SELECT * FROM notifications;

CREATE TABLE IF NOT EXISTS follows (
    id INT PRIMARY KEY AUTO_INCREMENT,
    follower_id INT NOT NULL,
    following_id INT NOT NULL,
    status ENUM('PENDING', 'ACCEPTED') DEFAULT 'ACCEPTED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_follow (follower_id, following_id),
    FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE
);


-- This query mimics exactly what Option 17 is doing
SELECT * FROM posts WHERE content LIKE '%#2%';

-- This will show you all the content the Java app is scanning
SELECT content FROM posts WHERE content LIKE '%#%';

-- Check total likes received for User ID 1 (replace 1 with your ID)
SELECT COUNT(*) 
FROM likes l 
JOIN posts p ON l.post_id = p.post_id 
WHERE p.user_id = 1;



DESCRIBE likes;

SELECT * FROM likes WHERE post_id IS NOT NULL;

--First, you need a table to store which user saved which po
CREATE TABLE IF NOT EXISTS saved_posts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_save (user_id, post_id), -- Prevents saving the same post twice
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE
);


-- Check if the bookmark link actually exists
SELECT * FROM saved_posts WHERE user_id = (SELECT id FROM users WHERE username = 'ben');


SELECT 
    u_saver.username AS saved_by, 
    u_author.username AS post_author, 
    p.content, 
    s.saved_at
FROM saved_posts s
JOIN users u_saver ON s.user_id = u_saver.id
JOIN posts p ON s.post_id = p.post_id
JOIN users u_author ON p.user_id = u_author.id;

SELECT * FROM saved_posts;

INSERT INTO users (username, password, email) 
VALUES ('admin', 'admin123', 'admin@revconnect.com');

SELECT * FROM users WHERE username = 'admin';

INSERT INTO users (username, password, email) VALUES ('admin', 'admin123', 'admin@revconnect.com');
DELETE FROM users WHERE username = 'admin';

SELECT p.*, u.username, COUNT(l.post_id) AS like_count 
FROM posts p 
JOIN users u ON p.user_id = u.id 
LEFT JOIN likes l ON p.post_id = l.post_id 
GROUP BY p.post_id 
ORDER BY like_count DESC, p.created_at DESC 
LIMIT 5;

CREATE TABLE IF NOT EXISTS saved_posts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_save (user_id, post_id), -- Prevents saving the same post twice
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE
);

DELETE FROM posts WHERE content LIKE 'Automated Test%';
SET SQL_SAFE_UPDATES = 0;

-- Now run your delete command
DELETE FROM posts WHERE content LIKE 'Automated Test%';

-- Optional: Turn it back on when done
SET SQL_SAFE_UPDATES = 1;

DELETE FROM posts WHERE content LIKE 'Automated Test%' LIMIT 50;

-- This removes all comments so you can start your demo with a clean slate