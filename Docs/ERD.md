

## 📊 1. ERD (Entity Relationship Diagram)

---

### 🏗️ Database Schema Visualization:

---

```text
┌─────────────────────┐         ┌─────────────────────┐
│        USERS        │         │        POSTS        │
├─────────────────────┤         ├─────────────────────┤
│ id (PK)             │◄────────┤ user_id (FK)        │
│ username (UNIQUE)   │         │ post_id (PK)        │
│ email (UNIQUE)      │         │ content             │
│ password            │         │ parent_post_id (FK) │
│ user_type (ENUM)    │         │ created_at          │
│ bio                 │         └─────────┬───────────┘
│ last_login          │                   │
└──────────┬──────────┘                   │
           │                              │
           ▼                              ▼
┌─────────────────────┐         ┌─────────────────────┐
│      COMMENTS       │         │        LIKES        │
├─────────────────────┤         ├─────────────────────┤
│ comment_id (PK)     │         │ id (PK)             │
│ post_id (FK)        │◄────────┤ post_id (FK)        │
│ user_id (FK)        │◄────────┤ user_id (FK)        │
│ content             │         └─────────────────────┘
│ created_at          │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐         ┌─────────────────────┐
│       FOLLOWS       │         │    SAVED_POSTS      │
├─────────────────────┤         ├─────────────────────┤
│ id (PK)             │         │ id (PK)             │
│ follower_id (FK)    │◄────────┤ user_id (FK)        │
│ following_id (FK)   │         │ post_id (FK)        │
│ status (ENUM)       │         │ saved_at            │
└─────────────────────┘         └─────────────────────┘
           │
           ▼
┌─────────────────────┐
│    NOTIFICATIONS    │
├─────────────────────┤
│ id (PK)             │
│ user_id (FK)        │
│ message             │
│ is_read             │
│ created_at          │
└─────────────────────┘

```

---

## 🔑 Relationships Summary

| RELATIONSHIP | TYPE | DESCRIPTION |
| --- | --- | --- |
| **Users → Posts** | **1:N** | A user can create multiple posts. |
| **Posts → Comments** | **1:N** | Each post can have multiple comments. |
| **Users → Comments** | **1:N** | A user can write comments on many different posts. |
| **Posts → Likes** | **1:N** | A post can be liked by many users. |
| **Users → Follows** | **M:N** | Users can follow many others and be followed back (Self-referencing). |
| **Posts → Posts** | **1:N** | Self-referencing relationship for shared/parent posts. |
| **Users → Notifications** | **1:N** | Users receive multiple alerts for activity like new followers or likes. |
| **Users → Saved Posts** | **1:N** | A user can bookmark multiple posts for their private collection. |

---

### 🛠️ Technical Design Notes

* **Data Integrity**: All foreign keys are set with `ON DELETE CASCADE`, meaning if a user is deleted, their posts, likes, and comments are automatically removed.
* **Recursive Relationship**: The `follows` table is a self-referencing many-to-many relationship using two foreign keys pointing back to the `users` table.
* **Security**: The `UNIQUE(user_id, post_id)` constraint on both `likes` and `saved_posts` prevents duplicate data entries and errors.
* **Content Sharing**: The `parent_post_id` in the `posts` table allows for a tree-like structure, enabling users to share existing content while maintaining a link to the original post.

