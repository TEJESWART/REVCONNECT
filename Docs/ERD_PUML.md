

---

## 📊 1. UPDATED ERD (Entity Relationship Diagram)

### 🏗️ Database Schema Visualization:

```text
┌──────────────────────────┐          ┌──────────────────────────┐
│          USERS           │          │          POSTS           │
├──────────────────────────┤          ├──────────────────────────┤
│ id (PK)                  │◄─────────┤ user_id (FK)             │
│ username (UNIQUE)        │          │ post_id (PK)             │
│ email (UNIQUE)           │          │ content                  │
│ password                 │          │ post_type (ENUM) [NEW]   │
│ user_type (ENUM)         │          │ parent_post_id (FK)      │
│ bio                      │          │ created_at               │
│ last_login               │          └──────────┬───────────────┘
│ category [NEW]           │                     │
│ address  [NEW]           │                     │
│ website  [NEW]           │                     ▼
│ hours    [NEW]           │          ┌──────────────────────────┐
└──────────┬───────────────┘          │          LIKES           │
           │                          ├──────────────────────────┤
           ▼                          │ id (PK)                  │
┌──────────────────────────┐          │ post_id (FK)             │
│         COMMENTS         │◄─────────┤ user_id (FK)             │
├──────────────────────────┤          └──────────────────────────┘
│ comment_id (PK)          │
│ post_id (FK)             │          ┌──────────────────────────┐
│ user_id (FK)             │          │       SAVED_POSTS        │
│ content                  │          ├──────────────────────────┤
│ created_at               │          │ id (PK)                  │
└──────────┬───────────────┘          │ user_id (FK)             │
           │                          │ post_id (FK)             │
           ▼                          │ saved_at                 │
┌──────────────────────────┐          └──────────────────────────┘
│         FOLLOWS          │
├──────────────────────────┤          ┌──────────────────────────┐
│ id (PK)                  │          │      NOTIFICATIONS       │
│ follower_id (FK)         │          ├──────────────────────────┤
│ following_id (FK)        │          │ id (PK)                  │
│ status (ENUM)            │          │ user_id (FK)             │
└──────────────────────────┘          │ message                  │
                                      │ is_read                  │
                                      │ created_at               │
                                      └──────────────────────────┘

```

---

## 🔑 Updated Relationships & Fields

| FIELD / RELATIONSHIP | TYPE | DESCRIPTION |
| --- | --- | --- |
| **User → Business Data** | **Attributes** | New fields (`category`, `website`, etc.) support the **Business Profile Setup** logic in `App.java`. |
| **Post → post_type** | **Attribute** | Stores "STANDARD", "PROMOTION", or "ANNOUNCEMENT" to trigger custom borders in `PostService`. |
| **Users → Posts** | **1:N** | A user can create multiple posts of varying types. |
| **Recursive Follows** | **M:N** | Self-referencing table allowing users to follow others. |

---

### 🛠️ Updated Technical Design Notes

* **Business Profile Normalization**: Business-specific fields in the `USERS` table are nullable. They remain empty for `PERSONAL` and `CREATOR` types but are mandatory for the `BUSINESS` registration flow in `AuthService`.
* **Post Categorization**: The `post_type` column is critical for the `renderFeed()` method. It allows the Service Layer to distinguish between regular content and official announcements/promotions.
* **Alias Implementation**: In the application logic, `User.id` is accessed via `getUserId()` to maintain consistency across the `PostService` and `InteractionService`.
* **Constraint Optimization**: To support the "B1" and "B2" business options, the `posts` table now includes a constraint to ensure only authorized IDs can insert non-standard `post_type` values.

