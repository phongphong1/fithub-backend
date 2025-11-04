SET NAMES utf8mb4;
SET time_zone = '+07:00';

DROP FUNCTION IF EXISTS `fn_get_config_decimal`;
DROP FUNCTION IF EXISTS `fn_get_config_int`;
DROP FUNCTION IF EXISTS `fn_get_platform_user_id`;

DROP TABLE IF EXISTS `trainer_applications`;
DROP TABLE IF EXISTS `reports`;
DROP TABLE IF EXISTS `lesson_progress`;
DROP TABLE IF EXISTS `course_reviews`;
DROP TABLE IF EXISTS `enrollments`;
DROP TABLE IF EXISTS `lessons`;
DROP TABLE IF EXISTS `courses`;
DROP TABLE IF EXISTS `course_categories`;
DROP TABLE IF EXISTS `payment_transactions`;
DROP TABLE IF EXISTS `withdrawal_requests`;
DROP TABLE IF EXISTS `payout_info`;
DROP TABLE IF EXISTS `coin_ledger`;
DROP TABLE IF EXISTS `wallet`;
DROP TABLE IF EXISTS `notifications`;
DROP TABLE IF EXISTS `interactions`;
DROP TABLE IF EXISTS `posts`;
DROP TABLE IF EXISTS `system_config`;
DROP TABLE IF EXISTS `tokens`;
DROP TABLE IF EXISTS `users`;

-- =================================================================
-- BẢNG CHÍNH: USERS
-- =================================================================
CREATE TABLE `users` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) UNIQUE NOT NULL,
  `password_hash` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) UNIQUE NOT NULL,
  `full_name` VARCHAR(100) NOT NULL,
  `date_of_birth` DATE NULL,
  `gender` ENUM('male', 'female', 'other') NULL,
  `avatar_url` VARCHAR(255) NULL,
  `cover_url` VARCHAR(255) NULL,
  `bio` TEXT NULL,
  `role` ENUM('gymer', 'trainer', 'admin', 'system_admin') NOT NULL DEFAULT 'gymer',
  `status` ENUM('active', 'inactive', 'banned') NOT NULL DEFAULT 'inactive',
  `last_login_at` DATETIME NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =================================================================
-- HỆ THỐNG QUẢN LÝ PHIÊN ĐĂNG NHẬP
-- =================================================================
CREATE TABLE `tokens` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `type` ENUM('refresh', 'reset_password', 'email_verification', 'other') NOT NULL,
  `token` VARCHAR(512) NOT NULL UNIQUE,
  `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
  `expires_at` DATETIME NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  CHECK (`expires_at` > `created_at`),
  INDEX `idx_tokens_user` (`user_id`),
  INDEX `idx_tokens_expires` (`expires_at`)
);

-- =================================================================
-- HỆ THỐNG CẤU HÌNH
-- =================================================================
CREATE TABLE `system_config` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `config_key` VARCHAR(100) UNIQUE NOT NULL,
  `config_value` TEXT NOT NULL,
  `data_type` ENUM('string', 'integer', 'decimal', 'boolean', 'json') NOT NULL DEFAULT 'string',
  `category` VARCHAR(50) NOT NULL,
  `description` VARCHAR(500) NULL,
  `is_editable` BOOLEAN NOT NULL DEFAULT TRUE,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  INDEX `idx_config_category` (`category`),
  INDEX `idx_config_key` (`config_key`)
);

-- =================================================================
-- HỆ THỐNG BÀI VIẾT (SOCIAL)
-- =================================================================
CREATE TABLE `posts` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `content` TEXT NOT NULL,
  `image_url` VARCHAR(255) NULL,
  `status` ENUM('pending', 'published', 'draft', 'archived', 'deleted', 'rejected') NOT NULL DEFAULT 'draft',
  `visibility` ENUM('public', 'private') NOT NULL DEFAULT 'public',
  `total_likes` INT NOT NULL DEFAULT 0,
  `total_comments` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  CHECK (`total_likes` >= 0),
  CHECK (`total_comments` >= 0),
  INDEX `idx_posts_user_status` (`user_id`, `status`),
  INDEX `idx_posts_status_created` (`status`, `created_at` DESC),
  INDEX `idx_posts_created` (`created_at` DESC)
);

CREATE TABLE `interactions` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `post_id` BIGINT NOT NULL,
  `parent_id` BIGINT NULL,
  `type` ENUM('like', 'comment', 'save') NOT NULL,
  `content` TEXT NULL,
  `media_url` VARCHAR(255) NULL,
  `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE,
  `deleted_at` DATETIME NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`post_id`) REFERENCES `posts`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`parent_id`) REFERENCES `interactions`(`id`) ON DELETE SET NULL,
  INDEX `idx_interactions_post_type` (`post_id`, `type`),
  INDEX `idx_interactions_user` (`user_id`),
  INDEX `idx_interactions_parent` (`parent_id`),
  INDEX `idx_interactions_created` (`created_at` DESC)
);

-- =================================================================
-- HỆ THỐNG THÔNG BÁO
-- =================================================================
CREATE TABLE `notifications` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `sender_id` BIGINT NULL,
  `type` ENUM('like', 'comment', 'course_enroll', 'lesson_complete', 'comment_replied', 'system_notification', 'other') NOT NULL,
  `reference_id` BIGINT NULL,
  `reference_type` VARCHAR(50) NULL,
  `content` TEXT NOT NULL,
  `is_read` BOOLEAN NOT NULL DEFAULT FALSE,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`sender_id`) REFERENCES `users`(`id`) ON DELETE SET NULL,
  INDEX `idx_notifications_user_read` (`user_id`, `is_read`),
  INDEX `idx_notifications_created` (`created_at` DESC)
);

-- =================================================================
-- HỆ THỐNG KHÓA HỌC (E-LEARNING)
-- =================================================================
CREATE TABLE `course_categories` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(100) UNIQUE NOT NULL,
  `description` TEXT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `courses` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `trainer_id` BIGINT NOT NULL,
  `category_id` BIGINT NULL,
  `parent_course_id` BIGINT NULL,
  `title` VARCHAR(255) NOT NULL,
  `description` TEXT NULL,
  `price` DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
  `thumbnail_url` VARCHAR(255) NULL,
  `difficulty_level` ENUM('beginner', 'intermediate', 'advanced') NOT NULL DEFAULT 'beginner',
  `status` ENUM('draft', 'published', 'archived') NOT NULL DEFAULT 'draft',
  `is_featured` BOOLEAN NOT NULL DEFAULT FALSE,
  `total_enrollments` INT NOT NULL DEFAULT 0,
  `average_rating` DECIMAL(3, 2) NULL,
  `total_reviews` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  FOREIGN KEY (`parent_course_id`) REFERENCES `courses`(`id`) ON DELETE SET NULL,
  FOREIGN KEY (`trainer_id`) REFERENCES `users`(`id`) ON DELETE RESTRICT,
  FOREIGN KEY (`category_id`) REFERENCES `course_categories`(`id`) ON DELETE SET NULL,
  CHECK (`price` >= 0),
  CHECK (`total_enrollments` >= 0),
  CHECK (`total_reviews` >= 0),
  CHECK (`average_rating` IS NULL OR (`average_rating` >= 1.00 AND `average_rating` <= 5.00)),
  INDEX `idx_courses_trainer_status` (`trainer_id`, `status`),
  INDEX `idx_courses_category` (`category_id`),
  INDEX `idx_courses_status` (`status`),
  INDEX `idx_courses_created` (`created_at` DESC)
);

CREATE TABLE `lessons` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `course_id` BIGINT NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `content_type` ENUM('video', 'article', 'practice') NOT NULL,
  `content_url` VARCHAR(255) NULL,
  `content_body` TEXT NULL,
  `order_index` INT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  FOREIGN KEY (`course_id`) REFERENCES `courses`(`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_course_order` (`course_id`, `order_index`),
  CHECK (`order_index` >= 1),
  INDEX `idx_lessons_course` (`course_id`)
);

CREATE TABLE `enrollments` (
  `user_id` BIGINT NOT NULL,
  `course_id` BIGINT NOT NULL,
  `enrolled_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `purchase_price` DECIMAL(10, 2) NOT NULL,
  `status` ENUM('active', 'completed') NOT NULL DEFAULT 'active',
  `progress_percentage` DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
  `completed_at` DATETIME NULL,
  
  PRIMARY KEY (`user_id`, `course_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`course_id`) REFERENCES `courses`(`id`) ON DELETE CASCADE,
  CHECK (`progress_percentage` >= 0 AND `progress_percentage` <= 100),
  CHECK (`purchase_price` >= 0),
  CHECK (`completed_at` IS NULL OR `enrolled_at` <= `completed_at`),
  INDEX `idx_enrollments_user_status` (`user_id`, `status`),
  INDEX `idx_enrollments_course` (`course_id`)
);

CREATE TABLE `lesson_progress` (
  `user_id` BIGINT NOT NULL,
  `lesson_id` BIGINT NOT NULL,
  `is_completed` BOOLEAN NOT NULL DEFAULT FALSE,
  `completed_at` DATETIME NULL,
  
  PRIMARY KEY (`user_id`, `lesson_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`lesson_id`) REFERENCES `lessons`(`id`) ON DELETE CASCADE,
  INDEX `idx_lesson_progress_lesson` (`lesson_id`),
  INDEX `idx_lesson_progress_user` (`user_id`)
);

CREATE TABLE `course_reviews` (
  `user_id` BIGINT NOT NULL,
  `course_id` BIGINT NOT NULL,
  `rating` TINYINT NOT NULL,
  `comment` TEXT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  
  PRIMARY KEY (`user_id`, `course_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`course_id`) REFERENCES `courses`(`id`) ON DELETE CASCADE,
  CHECK (`rating` >= 1 AND `rating` <= 5),
  INDEX `idx_course_reviews_course` (`course_id`),
  INDEX `idx_course_reviews_rating` (`rating`)
);

-- =================================================================
-- HỆ THỐNG VÍ & THANH TOÁN
-- =================================================================
CREATE TABLE `wallet` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL UNIQUE,
  `balance` DECIMAL(18, 4) NOT NULL DEFAULT 0.0000,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_wallet_user` (`user_id`)
);

CREATE TABLE `coin_ledger` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `wallet_id` BIGINT NOT NULL,
  `amount` DECIMAL(18, 4) NOT NULL,
  `transaction_type` ENUM('top_up', 'purchase', 'sale_revenue', 'withdrawal') NOT NULL,
  `reference_id` BIGINT NULL,
  `description` VARCHAR(255) NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  
  FOREIGN KEY (`wallet_id`) REFERENCES `wallet`(`id`) ON DELETE RESTRICT,
  CHECK (`amount` != 0),
  INDEX `idx_coin_ledger_wallet_created` (`wallet_id`, `created_at` DESC),
  INDEX `idx_coin_ledger_type` (`transaction_type`)
);

CREATE TABLE `payout_info` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `trainer_id` BIGINT NOT NULL,
  `bank_name` VARCHAR(100) NOT NULL,
  `account_number` VARCHAR(50) NOT NULL,
  `account_holder_name` VARCHAR(100) NOT NULL,
  `is_default` BOOLEAN NOT NULL DEFAULT FALSE,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  FOREIGN KEY (`trainer_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_trainer_account` (`trainer_id`, `account_number`)
);

CREATE TABLE `withdrawal_requests` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `trainer_id` BIGINT NOT NULL,
  `payout_info_id` BIGINT NOT NULL,
  `token_amount` DECIMAL(18, 4) NOT NULL,
  `vnd_amount` DECIMAL(12, 2) NOT NULL,
  `status` ENUM('pending', 'processed', 'rejected') NOT NULL DEFAULT 'pending',
  `requested_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `note` TEXT NULL,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  FOREIGN KEY (`trainer_id`) REFERENCES `users`(`id`) ON DELETE RESTRICT,
  FOREIGN KEY (`payout_info_id`) REFERENCES `payout_info`(`id`) ON DELETE RESTRICT,
  CHECK (`token_amount` > 0),
  CHECK (`vnd_amount` > 0),
  INDEX `idx_withdrawal_trainer_status` (`trainer_id`, `status`),
  INDEX `idx_withdrawal_status` (`status`)
);

CREATE TABLE `payment_transactions` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `amount` DECIMAL(18, 4) NOT NULL,
  `transaction_type` ENUM('deposit', 'purchase', 'refund', 'withdrawal') NOT NULL,
  `payment_method` ENUM('bank_transfer', 'credit_card', 'vnpay', 'zalopay') NULL,
  `status` ENUM('pending', 'processing', 'completed', 'failed', 'cancelled') NOT NULL DEFAULT 'pending',
  `reference_id` BIGINT NULL,
  `reference_type` VARCHAR(50) NULL,
  `gateway_transaction_id` VARCHAR(255) NULL,
  `gateway_response` JSON NULL,
  `description` VARCHAR(500) NULL,
  `error_message` TEXT NULL,
  `completed_at` DATETIME NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE RESTRICT,
  CHECK (`amount` != 0),
  INDEX `idx_payment_user` (`user_id`),
  INDEX `idx_payment_status` (`status`),
  INDEX `idx_payment_gateway` (`gateway_transaction_id`),
  INDEX `idx_payment_created` (`created_at` DESC),
  INDEX `idx_payment_reference` (`reference_type`, `reference_id`)
);

-- =================================================================
-- HỆ THỐNG KIỂM SOÁT (MODERATION)
-- =================================================================

CREATE TABLE `reports` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `reporter_user_id` BIGINT NOT NULL,
  `reported_id` BIGINT NOT NULL,
  `reported_type` ENUM('user', 'post', 'interaction', 'course') NOT NULL,
  `reason` TEXT NOT NULL,
  `status` ENUM('pending', 'reviewed', 'resolved', 'dismissed') NOT NULL DEFAULT 'pending',
  `admin_notes` TEXT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  FOREIGN KEY (`reporter_user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  CHECK (NOT (reported_type = 'user' AND reporter_user_id = reported_id)),
  INDEX `idx_reports_status` (`status`),
  INDEX `idx_reports_type_id` (`reported_type`, `reported_id`),
  INDEX `idx_reports_created` (`created_at` DESC)
);

CREATE TABLE `trainer_applications` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL UNIQUE,
  `qualifications` TEXT NOT NULL,
  `experience_details` TEXT NULL,
  `document_urls` JSON NULL,
  `status` ENUM('pending', 'approved', 'rejected') NOT NULL DEFAULT 'pending',
  `admin_feedback` TEXT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_trainer_applications_status` (`status`)
);



DELIMITER $$

-- =================================================================
-- STORED FUNCTIONS - ĐỌC SYSTEM CONFIG
-- =================================================================

-- Function: Lấy config dạng DECIMAL từ system_config
CREATE FUNCTION `fn_get_config_decimal`(config_key VARCHAR(100), default_value DECIMAL(18, 4))
RETURNS DECIMAL(18, 4)
READS SQL DATA
DETERMINISTIC
BEGIN
  DECLARE result DECIMAL(18, 4);
  
  SELECT CAST(config_value AS DECIMAL(18, 4)) INTO result
  FROM system_config
  WHERE system_config.config_key = config_key
  LIMIT 1;
  
  IF result IS NULL THEN
    RETURN default_value;
  END IF;
  
  RETURN result;
END$$

-- Function: Lấy config dạng INTEGER từ system_config
CREATE FUNCTION `fn_get_config_int`(config_key VARCHAR(100), default_value INT)
RETURNS INT
READS SQL DATA
DETERMINISTIC
BEGIN
  DECLARE result INT;
  
  SELECT CAST(config_value AS SIGNED) INTO result
  FROM system_config
  WHERE system_config.config_key = config_key
  LIMIT 1;
  
  IF result IS NULL THEN
    RETURN default_value;
  END IF;
  
  RETURN result;
END$$

-- Function: Lấy user_id của system admin (platform)
CREATE FUNCTION `fn_get_platform_user_id`()
RETURNS BIGINT
READS SQL DATA
DETERMINISTIC
BEGIN
  DECLARE result BIGINT;
  
  -- Tìm user có role = 'system_admin'
  SELECT id INTO result
  FROM users
  WHERE role = 'system_admin'
  LIMIT 1;
  
  -- Fallback: nếu không có, return user_id = 1
  IF result IS NULL THEN
    RETURN 1;
  END IF;
  
  RETURN result;
END$$

-- =================================================================
-- TRIGGERS
-- =================================================================

-- Trigger 1: Tự động tạo ví khi có người dùng mới
CREATE TRIGGER `trg_users_after_insert`
AFTER INSERT ON `users`
FOR EACH ROW
BEGIN
  INSERT INTO `wallet` (user_id, balance, updated_at)
  VALUES (NEW.id, 0.0000, CURRENT_TIMESTAMP);
END$$

-- Trigger 2: Tự động cập nhật số dư ví khi có giao dịch mới
CREATE TRIGGER `trg_coin_ledger_after_insert`
AFTER INSERT ON `coin_ledger`
FOR EACH ROW
BEGIN
  UPDATE `wallet`
  SET `balance` = `balance` + NEW.amount
  WHERE `id` = NEW.wallet_id;
END$$

-- Trigger 3: Cập nhật đếm like/comment khi THÊM interaction
CREATE TRIGGER `trg_interactions_after_insert`
AFTER INSERT ON `interactions`
FOR EACH ROW
BEGIN
  IF NEW.type = 'like' THEN
    UPDATE `posts` SET `total_likes` = `total_likes` + 1 WHERE `id` = NEW.post_id;
  ELSEIF NEW.type = 'comment' AND NEW.is_deleted = 0 THEN
    UPDATE `posts` SET `total_comments` = `total_comments` + 1 WHERE `id` = NEW.post_id;
  END IF;
END$$

-- Trigger 4: Cập nhật đếm like/comment khi XÓA CỨNG interaction (un-like)
CREATE TRIGGER `trg_interactions_after_delete`
AFTER DELETE ON `interactions`
FOR EACH ROW
BEGIN
  IF OLD.type = 'like' THEN
    UPDATE `posts` SET `total_likes` = `total_likes` - 1 WHERE `id` = OLD.post_id;
  ELSEIF OLD.type = 'comment' AND OLD.is_deleted = 0 THEN
    -- Chỉ trừ nếu nó là comment đang hiển thị
    UPDATE `posts` SET `total_comments` = `total_comments` - 1 WHERE `id` = OLD.post_id;
  END IF;
END$$

-- Trigger 5: Cập nhật đếm comment khi XÓA MỀM/KHÔI PHỤC (update is_deleted)
CREATE TRIGGER `trg_interactions_after_update`
AFTER UPDATE ON `interactions`
FOR EACH ROW
BEGIN
  IF OLD.type = 'comment' THEN
    -- Case 1: Xóa mềm một comment đang hiển thị
    IF OLD.is_deleted = 0 AND NEW.is_deleted = 1 THEN
      UPDATE `posts` SET `total_comments` = `total_comments` - 1 WHERE `id` = NEW.post_id;
    -- Case 2: Khôi phục một comment đã bị xóa mềm
    ELSEIF OLD.is_deleted = 1 AND NEW.is_deleted = 0 THEN
      UPDATE `posts` SET `total_comments` = `total_comments` + 1 WHERE `id` = NEW.post_id;
    END IF;
  END IF;
END$$

-- Trigger 6: Tự động nâng cấp Gymer lên Trainer khi đơn được duyệt
CREATE TRIGGER `trg_trainer_applications_after_update`
AFTER UPDATE ON `trainer_applications`
FOR EACH ROW
BEGIN
  -- Nếu đơn được duyệt (từ trạng thái khác chuyển sang 'approved')
  IF OLD.status != 'approved' AND NEW.status = 'approved' THEN
    UPDATE `users`
    SET `role` = 'trainer', `status` = 'active'
    WHERE `id` = NEW.user_id;
  END IF;
END$$

-- Trigger 7: Tự động xử lý chia doanh thu KHI MUA và cập nhật thống kê (ĐÃ CẢI TIẾN)
CREATE TRIGGER `trg_enrollments_after_insert`
AFTER INSERT ON `enrollments`
FOR EACH ROW
BEGIN
  DECLARE v_trainer_id BIGINT;
  DECLARE v_trainer_wallet_id BIGINT;
  DECLARE v_user_wallet_id BIGINT;
  DECLARE v_admin_wallet_id BIGINT;
  DECLARE v_admin_user_id BIGINT;
  DECLARE v_purchase_price DECIMAL(10, 2);
  DECLARE v_trainer_revenue DECIMAL(18, 4);
  DECLARE v_platform_fee DECIMAL(18, 4);
  DECLARE v_trainer_percentage DECIMAL(18, 4);
  DECLARE v_platform_percentage DECIMAL(18, 4);

  SET v_purchase_price = NEW.purchase_price;

  -- Cập nhật total_enrollments
  UPDATE `courses`
  SET `total_enrollments` = `total_enrollments` + 1
  WHERE `id` = NEW.course_id;

  -- Chỉ xử lý chia tiền nếu giá mua > 0
  IF v_purchase_price > 0 THEN
  
    -- 1. Lấy ID của PT từ khóa học
    SELECT `trainer_id` INTO v_trainer_id
    FROM `courses`
    WHERE `id` = NEW.course_id;
    
    -- 2. Lấy phần trăm chia revenue từ system_config (với fallback values)
    SET v_trainer_percentage = fn_get_config_decimal('trainer_revenue_percentage', 80.0);
    SET v_platform_percentage = fn_get_config_decimal('platform_fee_percentage', 20.0);
    
    -- 3. Lấy ID ví của các bên
    SELECT `id` INTO v_user_wallet_id FROM `wallet` WHERE `user_id` = NEW.user_id;
    SELECT `id` INTO v_trainer_wallet_id FROM `wallet` WHERE `user_id` = v_trainer_id;
    
    -- Lấy platform user_id từ function
    SET v_admin_user_id = fn_get_platform_user_id();
    SELECT `id` INTO v_admin_wallet_id FROM `wallet` WHERE `user_id` = v_admin_user_id LIMIT 1;

    -- 4. Tính toán doanh thu dựa trên config
    SET v_trainer_revenue = v_purchase_price * (v_trainer_percentage / 100);
    SET v_platform_fee = v_purchase_price * (v_platform_percentage / 100);

    -- 5. Tạo các bản ghi Sổ cái (coin_ledger)
    
    -- a) Trừ tiền của Gymer
    INSERT INTO `coin_ledger` (wallet_id, amount, transaction_type, reference_id, description)
    VALUES (v_user_wallet_id, -v_purchase_price, 'purchase', NEW.course_id, CONCAT('Purchase course ID: ', NEW.course_id));
    
    -- b) Cộng tiền cho PT
    INSERT INTO `coin_ledger` (wallet_id, amount, transaction_type, reference_id, description)
    VALUES (v_trainer_wallet_id, v_trainer_revenue, 'sale_revenue', NEW.course_id, CONCAT('Revenue from course ID: ', NEW.course_id));

    -- c) Cộng tiền phí cho Platform
    INSERT INTO `coin_ledger` (wallet_id, amount, transaction_type, reference_id, description)
    VALUES (v_admin_wallet_id, v_platform_fee, 'sale_revenue', NEW.course_id, CONCAT('Platform fee from course ID: ', NEW.course_id));

  END IF;
END$$

-- Trigger 8: Tự động cập nhật total_enrollments khi xóa enrollment
CREATE TRIGGER `trg_enrollments_after_delete`
AFTER DELETE ON `enrollments`
FOR EACH ROW
BEGIN
  UPDATE `courses`
  SET `total_enrollments` = `total_enrollments` - 1
  WHERE `id` = OLD.course_id;
END$$

-- Trigger 9: Tự động cập nhật average_rating và total_reviews khi có review mới
CREATE TRIGGER `trg_course_reviews_after_insert`
AFTER INSERT ON `course_reviews`
FOR EACH ROW
BEGIN
  UPDATE `courses`
  SET 
    `total_reviews` = `total_reviews` + 1,
    `average_rating` = (
      SELECT AVG(rating)
      FROM `course_reviews`
      WHERE `course_id` = NEW.course_id
    )
  WHERE `id` = NEW.course_id;
END$$

-- Trigger 10: Tự động cập nhật average_rating và total_reviews khi xóa review
CREATE TRIGGER `trg_course_reviews_after_delete`
AFTER DELETE ON `course_reviews`
FOR EACH ROW
BEGIN
  DECLARE v_avg_rating DECIMAL(3, 2);
  DECLARE v_total_reviews INT;
  
  SELECT AVG(rating), COUNT(*) INTO v_avg_rating, v_total_reviews
  FROM `course_reviews`
  WHERE `course_id` = OLD.course_id;
  
  UPDATE `courses`
  SET 
    `total_reviews` = v_total_reviews,
    `average_rating` = v_avg_rating
  WHERE `id` = OLD.course_id;
END$$

-- Trigger 11: Tự động cập nhật average_rating khi update review
CREATE TRIGGER `trg_course_reviews_after_update`
AFTER UPDATE ON `course_reviews`
FOR EACH ROW
BEGIN
  IF OLD.rating != NEW.rating THEN
    UPDATE `courses`
    SET `average_rating` = (
      SELECT AVG(rating)
      FROM `course_reviews`
      WHERE `course_id` = NEW.course_id
    )
    WHERE `id` = NEW.course_id;
  END IF;
END$$

-- Trigger 12: Tự động set completed_at khi lesson được đánh dấu hoàn thành
CREATE TRIGGER `trg_lesson_progress_after_insert`
AFTER INSERT ON `lesson_progress`
FOR EACH ROW
BEGIN
  IF NEW.is_completed = TRUE AND NEW.completed_at IS NULL THEN
    UPDATE `lesson_progress`
    SET `completed_at` = CURRENT_TIMESTAMP
    WHERE `user_id` = NEW.user_id AND `lesson_id` = NEW.lesson_id;
  END IF;
END$$

-- Trigger 13: Tự động cập nhật progress_percentage khi lesson_progress thay đổi
CREATE TRIGGER `trg_lesson_progress_after_update`
AFTER UPDATE ON `lesson_progress`
FOR EACH ROW
BEGIN
  DECLARE v_course_id BIGINT;
  DECLARE v_total_lessons INT;
  DECLARE v_completed_lessons INT;
  DECLARE v_progress DECIMAL(5, 2);
  
  -- Lấy course_id từ lesson
  SELECT `course_id` INTO v_course_id FROM `lessons` WHERE `id` = NEW.lesson_id;
  
  -- Đếm tổng số lessons trong course
  SELECT COUNT(*) INTO v_total_lessons FROM `lessons` WHERE `course_id` = v_course_id;
  
  -- Đếm số lessons đã hoàn thành
  SELECT COUNT(*) INTO v_completed_lessons
  FROM `lesson_progress` lp
  INNER JOIN `lessons` l ON lp.lesson_id = l.id
  WHERE l.course_id = v_course_id 
    AND lp.user_id = NEW.user_id 
    AND lp.is_completed = TRUE;
  
  -- Tính progress percentage
  IF v_total_lessons > 0 THEN
    SET v_progress = (v_completed_lessons * 100.0) / v_total_lessons;
    
    -- Cập nhật enrollments
    UPDATE `enrollments`
    SET `progress_percentage` = v_progress,
        `completed_at` = CASE 
          WHEN v_progress >= 100 AND `completed_at` IS NULL THEN CURRENT_TIMESTAMP
          WHEN v_progress < 100 THEN NULL
          ELSE `completed_at`
        END,
        `status` = CASE
          WHEN v_progress >= 100 THEN 'completed'
          ELSE `status`
        END
    WHERE `user_id` = NEW.user_id AND `course_id` = v_course_id;
  END IF;
  
  -- Set completed_at cho lesson_progress nếu chưa có
  IF NEW.is_completed = TRUE AND NEW.completed_at IS NULL THEN
    UPDATE `lesson_progress`
    SET `completed_at` = CURRENT_TIMESTAMP
    WHERE `user_id` = NEW.user_id AND `lesson_id` = NEW.lesson_id;
  END IF;
END$$

-- Trigger 14: Validate wallet balance trước khi tạo giao dịch rút tiền
CREATE TRIGGER `trg_coin_ledger_before_insert`
BEFORE INSERT ON `coin_ledger`
FOR EACH ROW
BEGIN
  DECLARE v_current_balance DECIMAL(18, 4);
  DECLARE v_new_balance DECIMAL(18, 4);
  
  -- Lấy số dư hiện tại
  SELECT `balance` INTO v_current_balance FROM `wallet` WHERE `id` = NEW.wallet_id;
  
  -- Tính số dư mới
  SET v_new_balance = v_current_balance + NEW.amount;
  
  -- Không cho phép số dư âm
  IF v_new_balance < 0 THEN
    SIGNAL SQLSTATE '45000'
    SET MESSAGE_TEXT = 'Insufficient balance. Current balance cannot be negative.';
  END IF;
END$$

-- Trigger 15: Prevent negative counters trong posts
CREATE TRIGGER `trg_posts_before_update`
BEFORE UPDATE ON `posts`
FOR EACH ROW
BEGIN
  IF NEW.total_likes < 0 THEN
    SET NEW.total_likes = 0;
  END IF;
  
  IF NEW.total_comments < 0 THEN
    SET NEW.total_comments = 0;
  END IF;
END$$

-- Trigger 16: Prevent negative counters trong courses
CREATE TRIGGER `trg_courses_before_update`
BEFORE UPDATE ON `courses`
FOR EACH ROW
BEGIN
  IF NEW.total_enrollments < 0 THEN
    SET NEW.total_enrollments = 0;
  END IF;
  
  IF NEW.total_reviews < 0 THEN
    SET NEW.total_reviews = 0;
  END IF;
END$$

DELIMITER ;