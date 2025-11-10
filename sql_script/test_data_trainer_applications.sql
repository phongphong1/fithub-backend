-- =====================================================
-- TEST DATA FOR TRAINER APPLICATIONS
-- =====================================================

-- Insert test users (Gymers who will apply to become Trainers)
INSERT INTO `users` (`username`, `password_hash`, `email`, `full_name`, `date_of_birth`, `gender`, `role`, `status`) 
VALUES
('nguyenvanan', '$2a$10$abcd1234567890', 'nguyenvanan@fithub.com', 'Nguyễn Văn An', '1990-05-15', 'MALE', 'GYMER', 'ACTIVE'),
('tranthbich', '$2a$10$abcd1234567891', 'tranthbich@fithub.com', 'Trần Thị Bích', '1992-08-20', 'FEMALE', 'GYMER', 'ACTIVE'),
('levancuong', '$2a$10$abcd1234567892', 'levancuong@fithub.com', 'Lê Văn Cường', '1988-03-10', 'MALE', 'TRAINER', 'ACTIVE'),
('phamthidung', '$2a$10$abcd1234567893', 'phamthidung@fithub.com', 'Phạm Thị Dung', '1995-11-25', 'FEMALE', 'GYMER', 'ACTIVE'),
('dominhem', '$2a$10$abcd1234567894', 'dominhem@fithub.com', 'Đỗ Minh Em', '1993-07-30', 'MALE', 'GYMER', 'ACTIVE'),
('phanhavy', '$2a$10$abcd1234567895', 'phanhavy@fithub.com', 'Phan Hà Vy', '1994-12-05', 'FEMALE', 'GYMER', 'ACTIVE');

-- Insert trainer applications
-- Note: Sử dụng actual user IDs từ database
-- Cần lấy ID sau khi insert users ở trên

-- Application 1: PENDING - Multiple certificates (ARRAY format)
INSERT INTO `trainer_applications` 
(`user_id`, `qualifications`, `experience_details`, `document_urls`, `status`, `admin_feedback`, `created_at`, `updated_at`)
SELECT 
  u.id,
  'ISSA Certified Personal Trainer; Fitness Nutrition Specialist',
  '5 năm kinh nghiệm làm PT tại các phòng gym lớn. Đã huấn luyện hơn 120+ học viên với nhiều mục tiêu khác nhau.',
  '{"certificates": ["https://r2.fithub.page/trainers/certificates/cv-a.pdf", "https://r2.fithub.page/trainers/certificates/cert-issa.pdf", "https://r2.fithub.page/trainers/certificates/exam-fns.pdf"]}',
  'PENDING',
  NULL,
  '2024-01-15 08:00:00',
  '2024-01-15 08:00:00'
FROM `users` u WHERE u.username = 'nguyenvanan';

-- Application 2: PENDING - Single certificate (ARRAY format)
INSERT INTO `trainer_applications` 
(`user_id`, `qualifications`, `experience_details`, `document_urls`, `status`, `admin_feedback`, `created_at`, `updated_at`)
SELECT 
  u.id,
  'NASM Certified Personal Trainer',
  '3 năm kinh nghiệm dạy HIIT và Strength Training cho nhóm nhỏ.',
  '{"certificates": ["https://example.com/files/cv-b.pdf"]}',
  'PENDING',
  NULL,
  '2024-01-14 07:00:00',
  '2024-01-14 07:00:00'
FROM `users` u WHERE u.username = 'tranthbich';

-- Application 3: APPROVED - Multiple certificates (ARRAY format)
INSERT INTO `trainer_applications` 
(`user_id`, `qualifications`, `experience_details`, `document_urls`, `status`, `admin_feedback`, `created_at`, `updated_at`)
SELECT 
  u.id,
  'ACSM Certified Personal Trainer; Strength & Conditioning Specialist',
  '7 năm chuyên về Strength Training và Bodybuilding.',
  '{"certificates": ["https://r2.fithub.page/trainers/certificates/c1.pdf", "https://r2.fithub.page/trainers/certificates/c2.pdf", "https://r2.fithub.page/trainers/certificates/c3.pdf"]}',
  'APPROVED',
  'Hồ sơ xuất sắc! Chào mừng bạn đến với đội ngũ PT.',
  '2024-01-13 09:30:00',
  '2024-01-13 10:00:00'
FROM `users` u WHERE u.username = 'levancuong';

-- Application 4: REJECTED - No certificates (EMPTY ARRAY)
INSERT INTO `trainer_applications` 
(`user_id`, `qualifications`, `experience_details`, `document_urls`, `status`, `admin_feedback`, `created_at`, `updated_at`)
SELECT 
  u.id,
  'Basic Fitness Certificate',
  NULL,
  '{"certificates": []}',
  'REJECTED',
  'Thiếu kinh nghiệm tối thiểu 1 năm và chứng chỉ chuyên môn.',
  '2024-01-12 06:00:00',
  '2024-01-12 06:15:00'
FROM `users` u WHERE u.username = 'phamthidung';

-- Application 5: PENDING - Single certificate (ARRAY format)
INSERT INTO `trainer_applications` 
(`user_id`, `qualifications`, `experience_details`, `document_urls`, `status`, `admin_feedback`, `created_at`, `updated_at`)
SELECT 
  u.id,
  'ACE Certified Personal Trainer',
  '2 năm kinh nghiệm Personal Training cá nhân.',
  '{"certificates": ["https://example.com/files/cv-e.pdf"]}',
  'PENDING',
  NULL,
  '2024-01-11 06:00:00',
  '2024-01-11 06:15:00'
FROM `users` u WHERE u.username = 'dominhem';

-- Application 6: PENDING - No certificates (EMPTY ARRAY)
INSERT INTO `trainer_applications` 
(`user_id`, `qualifications`, `experience_details`, `document_urls`, `status`, `admin_feedback`, `created_at`, `updated_at`)
SELECT 
  u.id,
  'CrossFit Level 1 Trainer',
  'Huấn luyện viên CrossFit với 1 năm kinh nghiệm.',
  '{"certificates": []}',
  'PENDING',
  NULL,
  '2024-01-10 06:00:00',
  '2024-01-10 06:15:00'
FROM `users` u WHERE u.username = 'phanhavy';

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================

-- Xem tất cả trainer applications
SELECT 
  ta.id,
  u.username,
  u.full_name,
  ta.qualifications,
  ta.status,
  ta.created_at
FROM trainer_applications ta
JOIN users u ON ta.user_id = u.id
ORDER BY ta.created_at DESC;

-- Count by status
SELECT 
  status,
  COUNT(*) as total
FROM trainer_applications
GROUP BY status;

