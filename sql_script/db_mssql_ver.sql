CREATE TABLE users (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    username NVARCHAR(50) UNIQUE NOT NULL,
    password_hash NVARCHAR(255) NOT NULL,
    email NVARCHAR(255) UNIQUE NOT NULL,
    full_name NVARCHAR(100) NOT NULL,
    date_of_birth DATE,
    gender NVARCHAR(10) CHECK (gender IN ('male', 'female', 'other')),
    avatar_url NVARCHAR(255),
    cover_url NVARCHAR(255),
    bio NTEXT, 
    [role] NVARCHAR(20) NOT NULL CHECK ([role] IN ('gymer', 'trainer', 'admin', 'system_admin')),
    [status] NVARCHAR(10) NOT NULL CHECK ([status] IN ('active', 'inactive', 'suspended')),
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE()
);

CREATE TABLE subscription_plans (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    [name] NVARCHAR(100) NOT NULL UNIQUE, 
    [description] NTEXT, 
    duration_days INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    benefits NTEXT,
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE()
);

CREATE TABLE user_subscriptions (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    [status] NVARCHAR(20) CHECK ([status] IN ('active', 'expired', 'cancelled', 'suspended')),
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (plan_id) REFERENCES subscription_plans(id) ON DELETE NO ACTION
);

CREATE TABLE subscription_payment (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    subscription_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method NVARCHAR(20) CHECK (payment_method IN ('bank_transfer', 'zalopay', 'vnpay')),
    transaction_id NVARCHAR(100) UNIQUE,
    payment_date DATETIME2 NOT NULL,
    [status] NVARCHAR(20) CHECK ([status] IN ('pending', 'completed', 'failed', 'refunded')),
    notes NTEXT, 
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),

    FOREIGN KEY (subscription_id) REFERENCES user_subscriptions(id) ON DELETE CASCADE
);

CREATE TABLE workout_sessions (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL,
    title NVARCHAR(255) NOT NULL,
    [description] NTEXT, 
    session_date DATE NOT NULL,
    start_time DATETIME2 NOT NULL,
    end_time DATETIME2,
    duration_minutes INT,
    [status] NVARCHAR(20) CHECK ([status] IN ('planned', 'in_progress', 'completed', 'cancelled')),
    notes NTEXT, 
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE exercises (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL, 
    [name] NVARCHAR(100) NOT NULL,
    [description] NTEXT, 
    category NVARCHAR(20) CHECK (category IN ('strength', 'cardio', 'flexibility', 'balance')),
    muscle_group NVARCHAR(100),
    equipment NVARCHAR(100),
    difficulty_level NVARCHAR(20) CHECK (difficulty_level IN ('beginner', 'intermediate', 'advanced')),
    video_url NVARCHAR(255),
    thumbnail_url NVARCHAR(255),
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),

    UNIQUE (user_id, [name]),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE NO ACTION
);

CREATE TABLE exercise_logs (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    workout_session_id BIGINT NOT NULL,
    exercise_id BIGINT NOT NULL,
    order_index INT NOT NULL,
    sets INT,
    reps INT,
    weight DECIMAL(6, 2),
    duration_minutes DECIMAL(6, 2),
    distance DECIMAL(6, 2),
    rest_seconds INT,
    notes NTEXT, 
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    
    UNIQUE (workout_session_id, order_index), 
    FOREIGN KEY (workout_session_id) REFERENCES workout_sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (exercise_id) REFERENCES exercises(id) ON DELETE NO ACTION
);

CREATE TABLE progress (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL,
    measurement_date DATE NOT NULL,
    weight DECIMAL(5, 2),
    height DECIMAL(5, 2),
    bmi DECIMAL(4, 2),
    notes NTEXT, 
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),

    UNIQUE (user_id, measurement_date), 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE goals (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL,
    title NVARCHAR(255) NOT NULL,
    [description] NTEXT, 
    goal_type NVARCHAR(20) NOT NULL CHECK (goal_type IN ('weight_loss', 'muscle_gain', 'endurance', 'strength', 'flexibility', 'custom')),
    target_value DECIMAL(10, 2),
    current_value DECIMAL(10, 2),
    unit NVARCHAR(20),
    start_date DATE,
    target_date DATE,
    [status] NVARCHAR(20) CHECK ([status] IN ('active', 'completed', 'abandoned')),
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE posts (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL,
    title NVARCHAR(255) NOT NULL,
    content NTEXT NOT NULL, 
    image_url NVARCHAR(255),
    [status] NVARCHAR(20) CHECK ([status] IN ('pending', 'published', 'draft', 'archived', 'deleted', 'rejected')),
    visibility NVARCHAR(10) CHECK (visibility IN ('public', 'friends', 'private')),
    total_likes INT DEFAULT 0,
    total_comments INT DEFAULT 0,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE tags (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    [name] NVARCHAR(50) UNIQUE NOT NULL,
    slug NVARCHAR(50) UNIQUE NOT NULL,
    usage_count INT DEFAULT 0,
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE()
);

CREATE TABLE post_tags (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    post_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    created_at DATETIME2 DEFAULT GETDATE(),
    
    UNIQUE (post_id, tag_id), 
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

CREATE TABLE interactions (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    parent_id BIGINT, 
    [type] NVARCHAR(10) NOT NULL CHECK ([type] IN ('like', 'comment', 'save')), 
    content NTEXT, 
    media_url NVARCHAR(255),
    is_deleted BIT DEFAULT 0,
    deleted_at DATETIME,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE NO ACTION,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES interactions(id) ON DELETE NO ACTION
);
GO
CREATE UNIQUE INDEX UQ_Interaction_Like
ON interactions (user_id, post_id, type) 
WHERE type IN ('like', 'save') AND is_deleted = 0;
GO

CREATE TABLE saved_posts (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    created_at DATETIME2 DEFAULT GETDATE(),
    
    UNIQUE (user_id, post_id), 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE NO ACTION,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

CREATE TABLE friendships (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL, 
    friend_id BIGINT NOT NULL, 
    [status] NVARCHAR(10) CHECK ([status] IN ('pending', 'accepted', 'rejected', 'blocked')),
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),

    UNIQUE (user_id, friend_id), 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE NO ACTION
);

CREATE TABLE notifications (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL, 
    sender_id BIGINT, 
    [type] NVARCHAR(30) CHECK ([type] IN ('like', 'comment', 'friend_request', 'friend_accept', 'goal_completed', 'subscription_expiring', 'mention')),
    reference_id BIGINT,
    reference_type NVARCHAR(50),
    content NTEXT, 
    is_read BIT DEFAULT 0,
    created_at DATETIME2 DEFAULT GETDATE(),

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE NO ACTION
);

CREATE TABLE reports (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    reporter_id BIGINT NOT NULL,
    reported_type NVARCHAR(10) NOT NULL CHECK (reported_type IN ('post', 'user')),
    reported_entity_id BIGINT NOT NULL,
    reason NVARCHAR(255) NOT NULL,
    details NTEXT, 
    [status] NVARCHAR(20) CHECK ([status] IN ('pending', 'in_review', 'resolved', 'rejected')),
    resolved_by BIGINT, 
    resolution_notes NTEXT, 
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),

    UNIQUE (reporter_id, reported_type, reported_entity_id), 
    FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE NO ACTION,
    FOREIGN KEY (resolved_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE user_activity_logs (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL,
    action_type NVARCHAR(100) NOT NULL,
    details NTEXT, 
    ip_address NVARCHAR(45),
    device_info NVARCHAR(255),
    created_at DATETIME2 DEFAULT GETDATE(),

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE system_logs (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    [level] NVARCHAR(10) NOT NULL CHECK ([level] IN ('INFO', 'WARNING', 'ERROR', 'CRITICAL')),
    [source] NVARCHAR(100) NOT NULL,
    [message] NTEXT NOT NULL, 
    context NTEXT, 
    created_at DATETIME2 DEFAULT GETDATE()
);

CREATE TABLE social_accounts (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL,
    provider NVARCHAR(20) NOT NULL CHECK (provider IN ('google', 'facebook')),
    provider_user_id NVARCHAR(255) NOT NULL,
    created_at DATETIME2 DEFAULT GETDATE(),    
    UNIQUE (provider, provider_user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
GO

-- ========================================================
-- ADDITIONAL CONSTRAINTS FOR DATA INTEGRITY
-- ========================================================

-- Subscription plans constraints
ALTER TABLE subscription_plans ADD CONSTRAINT CK_subscription_duration CHECK (duration_days > 0);
ALTER TABLE subscription_plans ADD CONSTRAINT CK_subscription_price CHECK (price >= 0);
GO

-- User subscriptions constraints
ALTER TABLE user_subscriptions ADD CONSTRAINT CK_subscription_dates CHECK (end_date >= start_date);
GO

-- Subscription payment constraints
ALTER TABLE subscription_payment ADD CONSTRAINT CK_payment_amount CHECK (amount >= 0);
GO

-- Workout sessions constraints
ALTER TABLE workout_sessions ADD CONSTRAINT CK_workout_times CHECK (end_time IS NULL OR end_time >= start_time);
ALTER TABLE workout_sessions ADD CONSTRAINT CK_workout_duration CHECK (duration_minutes IS NULL OR duration_minutes > 0);
GO

-- Exercise logs constraints
ALTER TABLE exercise_logs ADD CONSTRAINT CK_exercise_sets CHECK (sets IS NULL OR sets > 0);
ALTER TABLE exercise_logs ADD CONSTRAINT CK_exercise_reps CHECK (reps IS NULL OR reps > 0);
ALTER TABLE exercise_logs ADD CONSTRAINT CK_exercise_weight CHECK (weight IS NULL OR weight >= 0);
ALTER TABLE exercise_logs ADD CONSTRAINT CK_exercise_duration CHECK (duration_minutes IS NULL OR duration_minutes > 0);
ALTER TABLE exercise_logs ADD CONSTRAINT CK_exercise_distance CHECK (distance IS NULL OR distance > 0);
ALTER TABLE exercise_logs ADD CONSTRAINT CK_exercise_rest CHECK (rest_seconds IS NULL OR rest_seconds >= 0);
GO

-- Progress constraints
ALTER TABLE progress ADD CONSTRAINT CK_progress_weight CHECK (weight IS NULL OR weight > 0);
ALTER TABLE progress ADD CONSTRAINT CK_progress_height CHECK (height IS NULL OR height > 0);
ALTER TABLE progress ADD CONSTRAINT CK_progress_bmi CHECK (bmi IS NULL OR (bmi > 0 AND bmi < 100));
GO

-- Goals constraints
ALTER TABLE goals ADD CONSTRAINT CK_goals_dates CHECK (target_date IS NULL OR start_date IS NULL OR target_date >= start_date);
ALTER TABLE goals ADD CONSTRAINT CK_goals_target_value CHECK (target_value IS NULL OR target_value > 0);
ALTER TABLE goals ADD CONSTRAINT CK_goals_current_value CHECK (current_value IS NULL OR current_value >= 0);
GO

-- Posts constraints
ALTER TABLE posts ADD CONSTRAINT CK_posts_total_likes CHECK (total_likes >= 0);
ALTER TABLE posts ADD CONSTRAINT CK_posts_total_comments CHECK (total_comments >= 0);
GO

-- Tags constraints
ALTER TABLE tags ADD CONSTRAINT CK_tags_usage_count CHECK (usage_count >= 0);
ALTER TABLE tags ADD CONSTRAINT CK_tags_name_length CHECK (LEN([name]) > 1);
GO

-- Friendships constraints
ALTER TABLE friendships ADD CONSTRAINT CK_friendships_no_self CHECK (user_id != friend_id);
GO

-- ========================================================
-- TRIGGERS FOR AUTOMATION
-- ========================================================

-- 1. Auto-update updated_at timestamp
CREATE TRIGGER TR_users_updated ON users
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE users 
    SET updated_at = GETDATE() 
    WHERE id IN (SELECT id FROM inserted);
END;
GO

CREATE TRIGGER TR_subscription_plans_updated ON subscription_plans
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE subscription_plans 
    SET updated_at = GETDATE() 
    WHERE id IN (SELECT id FROM inserted);
END;
GO

CREATE TRIGGER TR_user_subscriptions_updated ON user_subscriptions
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE user_subscriptions 
    SET updated_at = GETDATE() 
    WHERE id IN (SELECT id FROM inserted);
END;
GO

CREATE TRIGGER TR_subscription_payment_updated ON subscription_payment
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE subscription_payment 
    SET updated_at = GETDATE() 
    WHERE id IN (SELECT id FROM inserted);
END;
GO

CREATE TRIGGER TR_workout_sessions_updated ON workout_sessions
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE workout_sessions 
    SET updated_at = GETDATE() 
    WHERE id IN (SELECT id FROM inserted);
END;
GO

CREATE TRIGGER TR_exercises_updated ON exercises
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE exercises 
    SET updated_at = GETDATE() 
    WHERE id IN (SELECT id FROM inserted);
END;
GO

CREATE TRIGGER TR_exercise_logs_updated ON exercise_logs
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE exercise_logs 
    SET updated_at = GETDATE() 
    WHERE id IN (SELECT id FROM inserted);
END;
GO

CREATE TRIGGER TR_progress_updated ON progress
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE progress 
    SET updated_at = GETDATE() 
    WHERE id IN (SELECT id FROM inserted);
END;
GO

CREATE TRIGGER TR_goals_updated ON goals
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE goals 
    SET updated_at = GETDATE() 
    WHERE id IN (SELECT id FROM inserted);
END;
GO

CREATE TRIGGER TR_posts_updated ON posts
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE posts 
    SET updated_at = GETDATE() 
    WHERE id IN (SELECT id FROM inserted);
END;
GO

CREATE TRIGGER TR_tags_updated ON tags
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE tags 
    SET updated_at = GETDATE() 
    WHERE id IN (SELECT id FROM inserted);
END;
GO

CREATE TRIGGER TR_interactions_updated ON interactions
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE interactions 
    SET updated_at = GETDATE() 
    WHERE id IN (SELECT id FROM inserted);
END;
GO

CREATE TRIGGER TR_friendships_updated ON friendships
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE friendships 
    SET updated_at = GETDATE() 
    WHERE id IN (SELECT id FROM inserted);
END;
GO

CREATE TRIGGER TR_reports_updated ON reports
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE reports 
    SET updated_at = GETDATE() 
    WHERE id IN (SELECT id FROM inserted);
END;
GO

-- 2. Auto-update post statistics (likes and comments count)
CREATE TRIGGER TR_interactions_post_stats ON interactions
AFTER INSERT, UPDATE, DELETE AS
BEGIN
    SET NOCOUNT ON;
    
    -- Update for inserted/updated records
    IF EXISTS (SELECT 1 FROM inserted)
    BEGIN
        UPDATE p
        SET total_likes = (
                SELECT COUNT(*) 
                FROM interactions 
                WHERE post_id = p.id 
                AND [type] = 'like' 
                AND is_deleted = 0
            ),
            total_comments = (
                SELECT COUNT(*) 
                FROM interactions 
                WHERE post_id = p.id 
                AND [type] = 'comment' 
                AND is_deleted = 0
            )
        FROM posts p
        WHERE p.id IN (SELECT DISTINCT post_id FROM inserted);
    END
    
    -- Update for deleted records
    IF EXISTS (SELECT 1 FROM deleted)
    BEGIN
        UPDATE p
        SET total_likes = (
                SELECT COUNT(*) 
                FROM interactions 
                WHERE post_id = p.id 
                AND [type] = 'like' 
                AND is_deleted = 0
            ),
            total_comments = (
                SELECT COUNT(*) 
                FROM interactions 
                WHERE post_id = p.id 
                AND [type] = 'comment' 
                AND is_deleted = 0
            )
        FROM posts p
        WHERE p.id IN (SELECT DISTINCT post_id FROM deleted);
    END
END;
GO

-- 3. Auto-update tag usage count
CREATE TRIGGER TR_post_tags_usage_count ON post_tags
AFTER INSERT, DELETE AS
BEGIN
    SET NOCOUNT ON;
    
    -- Update for inserted tags
    IF EXISTS (SELECT 1 FROM inserted)
    BEGIN
        UPDATE t
        SET usage_count = (SELECT COUNT(*) FROM post_tags WHERE tag_id = t.id)
        FROM tags t
        WHERE t.id IN (SELECT DISTINCT tag_id FROM inserted);
    END
    
    -- Update for deleted tags
    IF EXISTS (SELECT 1 FROM deleted)
    BEGIN
        UPDATE t
        SET usage_count = (SELECT COUNT(*) FROM post_tags WHERE tag_id = t.id)
        FROM tags t
        WHERE t.id IN (SELECT DISTINCT tag_id FROM deleted);
    END
END;
GO


-- 5. Handle sender deletion in notifications (set sender_id to NULL)
CREATE TRIGGER TR_users_delete_notifications ON users
AFTER DELETE AS
BEGIN
    SET NOCOUNT ON;
    
    -- Set sender_id to NULL for notifications where deleted user was sender
    UPDATE notifications
    SET sender_id = NULL
    WHERE sender_id IN (SELECT id FROM deleted);
END;
GO

-- 6. Auto-calculate workout duration
CREATE TRIGGER TR_workout_sessions_calculate_duration ON workout_sessions
AFTER INSERT, UPDATE AS
BEGIN
    SET NOCOUNT ON;
    
    UPDATE ws
    SET duration_minutes = DATEDIFF(MINUTE, ws.start_time, ws.end_time)
    FROM workout_sessions ws
    INNER JOIN inserted i ON ws.id = i.id
    WHERE ws.end_time IS NOT NULL 
    AND ws.start_time IS NOT NULL
    AND ws.end_time >= ws.start_time;
END;
GO

-- 7. Auto-calculate BMI
CREATE TRIGGER TR_progress_calculate_bmi ON progress
AFTER INSERT, UPDATE AS
BEGIN
    SET NOCOUNT ON;
    
    UPDATE p
    SET bmi = ROUND((p.weight / POWER(p.height / 100.0, 2)), 2)
    FROM progress p
    INNER JOIN inserted i ON p.id = i.id
    WHERE p.weight IS NOT NULL 
    AND p.height IS NOT NULL 
    AND p.height > 0;
END;
GO


-- ========================================================
-- INDEXES FOR PERFORMANCE OPTIMIZATION
-- ========================================================

-- Users table indexes
CREATE INDEX IX_users_email ON users(email);
CREATE INDEX IX_users_username ON users(username);
CREATE INDEX IX_users_role ON users([role]);
CREATE INDEX IX_users_status ON users([status]);
GO

-- Subscription related indexes
CREATE INDEX IX_user_subscriptions_user_id ON user_subscriptions(user_id);
CREATE INDEX IX_user_subscriptions_plan_id ON user_subscriptions(plan_id);
CREATE INDEX IX_user_subscriptions_status ON user_subscriptions([status]);
CREATE INDEX IX_user_subscriptions_dates ON user_subscriptions(start_date, end_date);
CREATE INDEX IX_subscription_payment_subscription_id ON subscription_payment(subscription_id);
CREATE INDEX IX_subscription_payment_status ON subscription_payment([status]);
GO

-- Workout related indexes
CREATE INDEX IX_workout_sessions_user_id ON workout_sessions(user_id);
CREATE INDEX IX_workout_sessions_date ON workout_sessions(session_date);
CREATE INDEX IX_workout_sessions_status ON workout_sessions([status]);
CREATE INDEX IX_exercises_user_id ON exercises(user_id);
CREATE INDEX IX_exercises_category ON exercises(category);
CREATE INDEX IX_exercise_logs_workout_session_id ON exercise_logs(workout_session_id);
CREATE INDEX IX_exercise_logs_exercise_id ON exercise_logs(exercise_id);
GO

-- Progress and Goals indexes
CREATE INDEX IX_progress_user_id ON progress(user_id);
CREATE INDEX IX_progress_date ON progress(measurement_date);
CREATE INDEX IX_goals_user_id ON goals(user_id);
CREATE INDEX IX_goals_status ON goals([status]);
GO

-- Posts and Social features indexes
CREATE INDEX IX_posts_user_id ON posts(user_id);
CREATE INDEX IX_posts_status ON posts([status]);
CREATE INDEX IX_posts_created_at ON posts(created_at DESC);
CREATE INDEX IX_posts_visibility ON posts(visibility);
CREATE INDEX IX_posts_status_created ON posts([status], created_at DESC); -- Composite index cho query phổ biến
GO

-- Tags and Post_tags indexes
CREATE INDEX IX_tags_slug ON tags(slug);
CREATE INDEX IX_post_tags_post_id ON post_tags(post_id);
CREATE INDEX IX_post_tags_tag_id ON post_tags(tag_id);
GO

-- Interactions indexes
CREATE INDEX IX_interactions_user_id ON interactions(user_id);
CREATE INDEX IX_interactions_post_id ON interactions(post_id);
CREATE INDEX IX_interactions_type ON interactions([type]);
CREATE INDEX IX_interactions_parent_id ON interactions(parent_id);
CREATE INDEX IX_interactions_post_type ON interactions(post_id, [type], is_deleted); -- Composite index cho likes/comments
CREATE INDEX IX_interactions_created_at ON interactions(created_at DESC);
GO

-- Saved posts indexes
CREATE INDEX IX_saved_posts_user_id ON saved_posts(user_id);
CREATE INDEX IX_saved_posts_post_id ON saved_posts(post_id);
GO

-- Friendships indexes
CREATE INDEX IX_friendships_user_id ON friendships(user_id);
CREATE INDEX IX_friendships_friend_id ON friendships(friend_id);
CREATE INDEX IX_friendships_status ON friendships([status]);
GO

-- Notifications indexes
CREATE INDEX IX_notifications_user_id ON notifications(user_id);
CREATE INDEX IX_notifications_sender_id ON notifications(sender_id);
CREATE INDEX IX_notifications_is_read ON notifications(is_read);
CREATE INDEX IX_notifications_created_at ON notifications(created_at DESC);
CREATE INDEX IX_notifications_user_unread ON notifications(user_id, is_read, created_at DESC); -- Composite index cho unread notifications
GO

-- Reports indexes
CREATE INDEX IX_reports_reporter_id ON reports(reporter_id);
CREATE INDEX IX_reports_status ON reports([status]);
CREATE INDEX IX_reports_reported_type ON reports(reported_type, reported_entity_id);
CREATE INDEX IX_reports_resolved_by ON reports(resolved_by);
GO

-- Activity logs indexes
CREATE INDEX IX_user_activity_logs_user_id ON user_activity_logs(user_id);
CREATE INDEX IX_user_activity_logs_action_type ON user_activity_logs(action_type);
CREATE INDEX IX_user_activity_logs_created_at ON user_activity_logs(created_at DESC);
GO

-- System logs indexes
CREATE INDEX IX_system_logs_level ON system_logs([level]);
CREATE INDEX IX_system_logs_source ON system_logs([source]);
CREATE INDEX IX_system_logs_created_at ON system_logs(created_at DESC);
GO

-- Social accounts indexes
CREATE INDEX IX_social_accounts_user_id ON social_accounts(user_id);
CREATE INDEX IX_social_accounts_provider ON social_accounts(provider, provider_user_id);
GO