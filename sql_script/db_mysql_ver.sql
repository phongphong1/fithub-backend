CREATE TABLE users (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       full_name VARCHAR(100) NOT NULL,
                       date_of_birth DATE,
                       gender ENUM('male', 'female', 'other'),
                       avatar_url VARCHAR(255),
                       cover_url VARCHAR(255),
                       bio TEXT,
                       `role` ENUM('gymer', 'trainer', 'admin', 'system_admin') NOT NULL,
                       `status` ENUM('active', 'inactive', 'banned') NOT NULL,
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

CREATE TABLE subscription_plans (
                                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                    `name` VARCHAR(100) NOT NULL UNIQUE,
                                    `description` TEXT,
                                    duration_days INT NOT NULL,
                                    price DECIMAL(10, 2) NOT NULL,
                                    benefits TEXT,
                                    is_active BOOLEAN DEFAULT TRUE,
                                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

CREATE TABLE user_subscriptions (
                                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                    user_id BIGINT NOT NULL,
                                    plan_id BIGINT NOT NULL,
                                    start_date DATE NOT NULL,
                                    end_date DATE NOT NULL,
                                    `status` ENUM('active', 'expired', 'cancelled', 'suspended'),
                                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                    FOREIGN KEY (plan_id) REFERENCES subscription_plans(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

CREATE TABLE subscription_payment (
                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      subscription_id BIGINT NOT NULL,
                                      amount DECIMAL(10, 2) NOT NULL,
                                      payment_method ENUM('bank_transfer', 'zalopay', 'vnpay'),
                                      transaction_id VARCHAR(100) UNIQUE,
                                      payment_date DATETIME NOT NULL,
                                      `status` ENUM('pending', 'completed', 'failed', 'refunded'),
                                      notes TEXT,
                                      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                      updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                      FOREIGN KEY (subscription_id) REFERENCES user_subscriptions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

CREATE TABLE workout_sessions (
                                  id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                  user_id BIGINT NOT NULL,
                                  title VARCHAR(255) NOT NULL,
                                  `description` TEXT,
                                  session_date DATE NOT NULL,
                                  start_time DATETIME NOT NULL,
                                  end_time DATETIME,
                                  duration_minutes INT,
                                  `status` ENUM('planned', 'in_progress', 'completed', 'cancelled'),
                                  notes TEXT,
                                  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

CREATE TABLE exercises (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           user_id BIGINT NOT NULL,
                           `name` VARCHAR(100) NOT NULL,
                           `description` TEXT,
                           category ENUM('strength', 'cardio', 'flexibility', 'balance'),
                           muscle_group VARCHAR(100),
                           equipment VARCHAR(100),
                           difficulty_level ENUM('beginner', 'intermediate', 'advanced'),
                           video_url VARCHAR(255),
                           thumbnail_url VARCHAR(255),
                           is_active BOOLEAN DEFAULT TRUE,
                           created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                           updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                           UNIQUE KEY unique_user_exercise (user_id, `name`),
                           FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

CREATE TABLE exercise_logs (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               workout_session_id BIGINT NOT NULL,
                               exercise_id BIGINT NOT NULL,
                               order_index INT NOT NULL,
                               sets INT,
                               reps INT,
                               weight DECIMAL(6, 2),
                               duration_minutes DECIMAL(6, 2),
                               distance DECIMAL(6, 2),
                               rest_seconds INT,
                               notes TEXT,
                               created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                               updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                               UNIQUE KEY unique_workout_order (workout_session_id, order_index),
                               FOREIGN KEY (workout_session_id) REFERENCES workout_sessions(id) ON DELETE CASCADE,
                               FOREIGN KEY (exercise_id) REFERENCES exercises(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

CREATE TABLE progress (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          user_id BIGINT NOT NULL,
                          measurement_date DATE NOT NULL,
                          weight DECIMAL(5, 2),
                          height DECIMAL(5, 2),
                          bmi DECIMAL(4, 2),
                          notes TEXT,
                          created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                          updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                          UNIQUE KEY unique_user_measurement (user_id, measurement_date),
                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

CREATE TABLE goals (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       user_id BIGINT NOT NULL,
                       title VARCHAR(255) NOT NULL,
                       `description` TEXT,
                       goal_type ENUM('weight_loss', 'muscle_gain', 'endurance', 'strength', 'flexibility', 'custom') NOT NULL,
                       target_value DECIMAL(10, 2),
                       current_value DECIMAL(10, 2),
                       unit VARCHAR(20),
                       start_date DATE,
                       target_date DATE,
                       `status` ENUM('active', 'completed', 'abandoned'),
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

CREATE TABLE posts (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       user_id BIGINT NOT NULL,
                       title VARCHAR(255) NOT NULL,
                       content TEXT NOT NULL,
                       image_url VARCHAR(255),
                       `status` ENUM('pending', 'published', 'draft', 'archived', 'deleted', 'rejected'),
                       visibility ENUM('public', 'friends', 'private'),
                       total_likes INT DEFAULT 0,
                       total_comments INT DEFAULT 0,
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

CREATE TABLE tags (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      `name` VARCHAR(50) UNIQUE NOT NULL,
                      slug VARCHAR(50) UNIQUE NOT NULL,
                      usage_count INT DEFAULT 0,
                      is_active BOOLEAN DEFAULT TRUE,
                      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                      updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

CREATE TABLE post_tags (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           post_id BIGINT NOT NULL,
                           tag_id BIGINT NOT NULL,
                           created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

                           UNIQUE KEY unique_post_tag (post_id, tag_id),
                           FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                           FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

CREATE TABLE interactions (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              user_id BIGINT NOT NULL,
                              post_id BIGINT NOT NULL,
                              parent_id BIGINT,
                              `type` ENUM('like', 'comment', 'save') NOT NULL,
                              content TEXT,
                              media_url VARCHAR(255),
                              is_deleted BOOLEAN DEFAULT FALSE,
                              deleted_at DATETIME,
                              created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                              updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                              FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
                              FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                              FOREIGN KEY (parent_id) REFERENCES interactions(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

-- Note: Unique index cho interactions like/save
CREATE UNIQUE INDEX UQ_Interaction_Like
    ON interactions (user_id, post_id, `type`, is_deleted);

CREATE TABLE saved_posts (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             user_id BIGINT NOT NULL,
                             post_id BIGINT NOT NULL,
                             created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

                             UNIQUE KEY unique_user_saved_post (user_id, post_id),
                             FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
                             FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

CREATE TABLE friendships (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             user_id BIGINT NOT NULL,
                             friend_id BIGINT NOT NULL,
                             `status` ENUM('pending', 'accepted', 'rejected', 'blocked'),
                             created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                             updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                             UNIQUE KEY unique_user_friend (user_id, friend_id),
                             FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                             FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

CREATE TABLE notifications (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               user_id BIGINT NOT NULL,
                               sender_id BIGINT,
                               `type` ENUM('like', 'comment', 'friend_request', 'friend_accept', 'goal_completed', 'subscription_expiring', 'mention'),
                               reference_id BIGINT,
                               reference_type VARCHAR(50),
                               content TEXT,
                               is_read BOOLEAN DEFAULT FALSE,
                               created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

                               FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                               FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

CREATE TABLE reports (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         reporter_id BIGINT NOT NULL,
                         reported_type ENUM('post', 'user') NOT NULL,
                         reported_entity_id BIGINT NOT NULL,
                         reason VARCHAR(255) NOT NULL,
                         details TEXT,
                         `status` ENUM('pending', 'in_review', 'resolved', 'rejected'),
                         resolved_by BIGINT,
                         resolution_notes TEXT,
                         created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                         updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                         UNIQUE KEY unique_report (reporter_id, reported_type, reported_entity_id),
                         FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE RESTRICT,
                         FOREIGN KEY (resolved_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

CREATE TABLE user_activity_logs (
                                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                    user_id BIGINT NOT NULL,
                                    action_type VARCHAR(100) NOT NULL,
                                    details TEXT,
                                    ip_address VARCHAR(45),
                                    device_info VARCHAR(255),
                                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

                                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

CREATE TABLE system_logs (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             `level` ENUM('INFO', 'WARNING', 'ERROR', 'CRITICAL') NOT NULL,
                             `source` VARCHAR(100) NOT NULL,
                             `message` TEXT NOT NULL,
                             context TEXT,
                             created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

CREATE TABLE social_accounts (
                                 id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 user_id BIGINT NOT NULL,
                                 provider ENUM('google', 'facebook') NOT NULL,
                                 provider_user_id VARCHAR(255) NOT NULL,
                                 created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                 UNIQUE KEY unique_provider_user (provider, provider_user_id),
                                 FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

-- ========================================================
-- ADDITIONAL CONSTRAINTS FOR DATA INTEGRITY
-- ========================================================

-- Subscription plans constraints
ALTER TABLE subscription_plans ADD CONSTRAINT CK_subscription_duration CHECK (duration_days > 0);
ALTER TABLE subscription_plans ADD CONSTRAINT CK_subscription_price CHECK (price >= 0);

-- User subscriptions constraints
ALTER TABLE user_subscriptions ADD CONSTRAINT CK_subscription_dates CHECK (end_date >= start_date);

-- Subscription payment constraints
ALTER TABLE subscription_payment ADD CONSTRAINT CK_payment_amount CHECK (amount >= 0);

-- Workout sessions constraints
ALTER TABLE workout_sessions ADD CONSTRAINT CK_workout_times CHECK (end_time IS NULL OR end_time >= start_time);
ALTER TABLE workout_sessions ADD CONSTRAINT CK_workout_duration CHECK (duration_minutes IS NULL OR duration_minutes > 0);

-- Exercise logs constraints
ALTER TABLE exercise_logs ADD CONSTRAINT CK_exercise_sets CHECK (sets IS NULL OR sets > 0);
ALTER TABLE exercise_logs ADD CONSTRAINT CK_exercise_reps CHECK (reps IS NULL OR reps > 0);
ALTER TABLE exercise_logs ADD CONSTRAINT CK_exercise_weight CHECK (weight IS NULL OR weight >= 0);
ALTER TABLE exercise_logs ADD CONSTRAINT CK_exercise_duration CHECK (duration_minutes IS NULL OR duration_minutes > 0);
ALTER TABLE exercise_logs ADD CONSTRAINT CK_exercise_distance CHECK (distance IS NULL OR distance > 0);
ALTER TABLE exercise_logs ADD CONSTRAINT CK_exercise_rest CHECK (rest_seconds IS NULL OR rest_seconds >= 0);

-- Progress constraints
ALTER TABLE progress ADD CONSTRAINT CK_progress_weight CHECK (weight IS NULL OR weight > 0);
ALTER TABLE progress ADD CONSTRAINT CK_progress_height CHECK (height IS NULL OR height > 0);
ALTER TABLE progress ADD CONSTRAINT CK_progress_bmi CHECK (bmi IS NULL OR (bmi > 0 AND bmi < 100));

-- Goals constraints
ALTER TABLE goals ADD CONSTRAINT CK_goals_dates CHECK (target_date IS NULL OR start_date IS NULL OR target_date >= start_date);
ALTER TABLE goals ADD CONSTRAINT CK_goals_target_value CHECK (target_value IS NULL OR target_value > 0);
ALTER TABLE goals ADD CONSTRAINT CK_goals_current_value CHECK (current_value IS NULL OR current_value >= 0);

-- Posts constraints
ALTER TABLE posts ADD CONSTRAINT CK_posts_total_likes CHECK (total_likes >= 0);
ALTER TABLE posts ADD CONSTRAINT CK_posts_total_comments CHECK (total_comments >= 0);

-- Tags constraints
ALTER TABLE tags ADD CONSTRAINT CK_tags_usage_count CHECK (usage_count >= 0);
ALTER TABLE tags ADD CONSTRAINT CK_tags_name_length CHECK (LENGTH(`name`) > 1);

-- Friendships constraints
ALTER TABLE friendships ADD CONSTRAINT CK_friendships_no_self CHECK (user_id != friend_id);

-- ========================================================
-- TRIGGERS FOR AUTOMATION
-- ========================================================

DELIMITER $$

-- 1. Auto-update post statistics (likes and comments count)
CREATE TRIGGER TR_interactions_post_stats_insert
    AFTER INSERT ON interactions
    FOR EACH ROW
BEGIN
    UPDATE posts p
    SET total_likes = (
        SELECT COUNT(*)
        FROM interactions
        WHERE post_id = NEW.post_id
          AND `type` = 'like'
          AND is_deleted = FALSE
    ),
        total_comments = (
            SELECT COUNT(*)
            FROM interactions
            WHERE post_id = NEW.post_id
              AND `type` = 'comment'
              AND is_deleted = FALSE
        )
    WHERE p.id = NEW.post_id;
END$$

CREATE TRIGGER TR_interactions_post_stats_update
    AFTER UPDATE ON interactions
    FOR EACH ROW
BEGIN
    UPDATE posts p
    SET total_likes = (
        SELECT COUNT(*)
        FROM interactions
        WHERE post_id = NEW.post_id
          AND `type` = 'like'
          AND is_deleted = FALSE
    ),
        total_comments = (
            SELECT COUNT(*)
            FROM interactions
            WHERE post_id = NEW.post_id
              AND `type` = 'comment'
              AND is_deleted = FALSE
        )
    WHERE p.id = NEW.post_id;
END$$

CREATE TRIGGER TR_interactions_post_stats_delete
    AFTER DELETE ON interactions
    FOR EACH ROW
BEGIN
    UPDATE posts p
    SET total_likes = (
        SELECT COUNT(*)
        FROM interactions
        WHERE post_id = OLD.post_id
          AND `type` = 'like'
          AND is_deleted = FALSE
    ),
        total_comments = (
            SELECT COUNT(*)
            FROM interactions
            WHERE post_id = OLD.post_id
              AND `type` = 'comment'
              AND is_deleted = FALSE
        )
    WHERE p.id = OLD.post_id;
END$$

-- 2. Auto-update tag usage count
CREATE TRIGGER TR_post_tags_usage_count_insert
    AFTER INSERT ON post_tags
    FOR EACH ROW
BEGIN
    UPDATE tags t
    SET usage_count = (SELECT COUNT(*) FROM post_tags WHERE tag_id = NEW.tag_id)
    WHERE t.id = NEW.tag_id;
END$$

CREATE TRIGGER TR_post_tags_usage_count_delete
    AFTER DELETE ON post_tags
    FOR EACH ROW
BEGIN
    UPDATE tags t
    SET usage_count = (SELECT COUNT(*) FROM post_tags WHERE tag_id = OLD.tag_id)
    WHERE t.id = OLD.tag_id;
END$$

-- 3. Auto-calculate workout duration
CREATE TRIGGER TR_workout_sessions_calculate_duration_insert
    BEFORE INSERT ON workout_sessions
    FOR EACH ROW
BEGIN
    IF NEW.end_time IS NOT NULL AND NEW.start_time IS NOT NULL AND NEW.end_time >= NEW.start_time THEN
        SET NEW.duration_minutes = TIMESTAMPDIFF(MINUTE, NEW.start_time, NEW.end_time);
    END IF;
END$$

CREATE TRIGGER TR_workout_sessions_calculate_duration_update
    BEFORE UPDATE ON workout_sessions
    FOR EACH ROW
BEGIN
    IF NEW.end_time IS NOT NULL AND NEW.start_time IS NOT NULL AND NEW.end_time >= NEW.start_time THEN
        SET NEW.duration_minutes = TIMESTAMPDIFF(MINUTE, NEW.start_time, NEW.end_time);
    END IF;
END$$

-- 4. Auto-calculate BMI
CREATE TRIGGER TR_progress_calculate_bmi_insert
    BEFORE INSERT ON progress
    FOR EACH ROW
BEGIN
    IF NEW.weight IS NOT NULL AND NEW.height IS NOT NULL AND NEW.height > 0 THEN
        SET NEW.bmi = ROUND((NEW.weight / POW(NEW.height / 100.0, 2)), 2);
    END IF;
END$$

CREATE TRIGGER TR_progress_calculate_bmi_update
    BEFORE UPDATE ON progress
    FOR EACH ROW
BEGIN
    IF NEW.weight IS NOT NULL AND NEW.height IS NOT NULL AND NEW.height > 0 THEN
        SET NEW.bmi = ROUND((NEW.weight / POW(NEW.height / 100.0, 2)), 2);
    END IF;
END$$

DELIMITER ;

-- ========================================================
-- INDEXES FOR PERFORMANCE OPTIMIZATION
-- ========================================================

-- Users table indexes
CREATE INDEX IX_users_email ON users(email);
CREATE INDEX IX_users_username ON users(username);
CREATE INDEX IX_users_role ON users(`role`);
CREATE INDEX IX_users_status ON users(`status`);

-- Subscription related indexes
CREATE INDEX IX_user_subscriptions_user_id ON user_subscriptions(user_id);
CREATE INDEX IX_user_subscriptions_plan_id ON user_subscriptions(plan_id);
CREATE INDEX IX_user_subscriptions_status ON user_subscriptions(`status`);
CREATE INDEX IX_user_subscriptions_dates ON user_subscriptions(start_date, end_date);
CREATE INDEX IX_subscription_payment_subscription_id ON subscription_payment(subscription_id);
CREATE INDEX IX_subscription_payment_status ON subscription_payment(`status`);

-- Workout related indexes
CREATE INDEX IX_workout_sessions_user_id ON workout_sessions(user_id);
CREATE INDEX IX_workout_sessions_date ON workout_sessions(session_date);
CREATE INDEX IX_workout_sessions_status ON workout_sessions(`status`);
CREATE INDEX IX_exercises_user_id ON exercises(user_id);
CREATE INDEX IX_exercises_category ON exercises(category);
CREATE INDEX IX_exercise_logs_workout_session_id ON exercise_logs(workout_session_id);
CREATE INDEX IX_exercise_logs_exercise_id ON exercise_logs(exercise_id);

-- Progress and Goals indexes
CREATE INDEX IX_progress_user_id ON progress(user_id);
CREATE INDEX IX_progress_date ON progress(measurement_date);
CREATE INDEX IX_goals_user_id ON goals(user_id);
CREATE INDEX IX_goals_status ON goals(`status`);

-- Posts and Social features indexes
CREATE INDEX IX_posts_user_id ON posts(user_id);
CREATE INDEX IX_posts_status ON posts(`status`);
CREATE INDEX IX_posts_created_at ON posts(created_at DESC);
CREATE INDEX IX_posts_visibility ON posts(visibility);
CREATE INDEX IX_posts_status_created ON posts(`status`, created_at DESC);

-- Tags and Post_tags indexes
CREATE INDEX IX_tags_slug ON tags(slug);
CREATE INDEX IX_post_tags_post_id ON post_tags(post_id);
CREATE INDEX IX_post_tags_tag_id ON post_tags(tag_id);

-- Interactions indexes
CREATE INDEX IX_interactions_user_id ON interactions(user_id);
CREATE INDEX IX_interactions_post_id ON interactions(post_id);
CREATE INDEX IX_interactions_type ON interactions(`type`);
CREATE INDEX IX_interactions_parent_id ON interactions(parent_id);
CREATE INDEX IX_interactions_post_type ON interactions(post_id, `type`, is_deleted);
CREATE INDEX IX_interactions_created_at ON interactions(created_at DESC);

-- Saved posts indexes
CREATE INDEX IX_saved_posts_user_id ON saved_posts(user_id);
CREATE INDEX IX_saved_posts_post_id ON saved_posts(post_id);

-- Friendships indexes
CREATE INDEX IX_friendships_user_id ON friendships(user_id);
CREATE INDEX IX_friendships_friend_id ON friendships(friend_id);
CREATE INDEX IX_friendships_status ON friendships(`status`);

-- Notifications indexes
CREATE INDEX IX_notifications_user_id ON notifications(user_id);
CREATE INDEX IX_notifications_sender_id ON notifications(sender_id);
CREATE INDEX IX_notifications_is_read ON notifications(is_read);
CREATE INDEX IX_notifications_created_at ON notifications(created_at DESC);
CREATE INDEX IX_notifications_user_unread ON notifications(user_id, is_read, created_at DESC);

-- Reports indexes
CREATE INDEX IX_reports_reporter_id ON reports(reporter_id);
CREATE INDEX IX_reports_status ON reports(`status`);
CREATE INDEX IX_reports_reported_type ON reports(reported_type, reported_entity_id);
CREATE INDEX IX_reports_resolved_by ON reports(resolved_by);

-- Activity logs indexes
CREATE INDEX IX_user_activity_logs_user_id ON user_activity_logs(user_id);
CREATE INDEX IX_user_activity_logs_action_type ON user_activity_logs(action_type);
CREATE INDEX IX_user_activity_logs_created_at ON user_activity_logs(created_at DESC);

-- System logs indexes
CREATE INDEX IX_system_logs_level ON system_logs(`level`);
CREATE INDEX IX_system_logs_source ON system_logs(`source`);
CREATE INDEX IX_system_logs_created_at ON system_logs(created_at DESC);

-- Social accounts indexes
CREATE INDEX IX_social_accounts_user_id ON social_accounts(user_id);
CREATE INDEX IX_social_accounts_provider ON social_accounts(provider, provider_user_id);
