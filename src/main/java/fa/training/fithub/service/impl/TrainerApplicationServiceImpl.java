package fa.training.fithub.service.impl;

import fa.training.fithub.dto.request.ProcessApplicationRequest;
import fa.training.fithub.dto.request.TrainerApplicationRequest;
import fa.training.fithub.dto.response.TrainerApplicationResponse;
import fa.training.fithub.entity.TrainerApplication;
import fa.training.fithub.entity.User;
import fa.training.fithub.enums.ApplicationStatus;
import fa.training.fithub.enums.Role;
import fa.training.fithub.exception.BadRequestException;
import fa.training.fithub.exception.ForbiddenException;
import fa.training.fithub.exception.UserNotFoundException;
import fa.training.fithub.repository.TrainerApplicationRepository;
import fa.training.fithub.repository.UserRepository;
import fa.training.fithub.service.TrainerApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TrainerApplicationServiceImpl implements TrainerApplicationService {

    private final TrainerApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final EmailNotificationService emailNotificationService;

    @Override
    @Transactional
    public TrainerApplicationResponse submitApplication(TrainerApplicationRequest request, Long userId) {
        // 1. Tìm user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // 2. Kiểm tra user phải là GYMER
        if (user.getRole() != Role.GYMER) {
            throw new ForbiddenException("Only Gymers can apply to become Trainers");
        }

        // 3. Kiểm tra đã apply chưa
        if (applicationRepository.existsByUser(user)) {
            throw new BadRequestException("You have already submitted a trainer application");
        }

        // 4. Tạo application mới
        TrainerApplication application = new TrainerApplication();
        application.setUser(user);
        application.setQualifications(request.getQualifications());
        application.setExperienceDetails(request.getExperienceDetails());
        application.setDocumentUrls(request.getDocumentUrls());
        application.setStatus(ApplicationStatus.PENDING);

        TrainerApplication savedApplication = applicationRepository.save(application);

        // 5. Gửi notification cho Admin (optional)
        // TODO: Implement notification service

        return mapToResponse(savedApplication);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerApplicationResponse getMyApplication(Long userId) {
        TrainerApplication application = applicationRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("No application found"));

        return mapToResponse(application);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrainerApplicationResponse> getAllApplications(ApplicationStatus status, Pageable pageable) {
        Page<TrainerApplication> applications;

        if (status != null) {
            applications = applicationRepository.findAllByStatus(status, pageable);
        } else {
            applications = applicationRepository.findAll(pageable);
        }

        return applications.map(this::mapToResponse);
    }

    @Override
    @Transactional
    public TrainerApplicationResponse processApplication(Long applicationId, ProcessApplicationRequest request) {
        // 1. Tìm application
        TrainerApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new BadRequestException("Application not found"));

        // 2. Kiểm tra status phải là PENDING
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new BadRequestException("Application has already been processed");
        }

        // 3. Validate status request
        ApplicationStatus newStatus = request.getStatus();
        if (newStatus != ApplicationStatus.APPROVED && newStatus != ApplicationStatus.REJECTED) {
            throw new BadRequestException("Status must be APPROVED or REJECTED");
        }

        // 4. Nếu REJECT thì bắt buộc phải có feedback
        if (newStatus == ApplicationStatus.REJECTED &&
                (request.getAdminFeedback() == null || request.getAdminFeedback().trim().isEmpty())) {
            throw new BadRequestException("Admin feedback is required when rejecting application");
        }

        // 5. Update application
        application.setStatus(newStatus);
        application.setAdminFeedback(request.getAdminFeedback());

        TrainerApplication updatedApplication = applicationRepository.save(application);

        // 6. Trigger sẽ tự động upgrade user nếu APPROVED
        // (trg_trainer_applications_after_update trong database.sql)

        // 7. Gửi email notification cho user
        sendApplicationResultEmail(updatedApplication);

        return mapToResponse(updatedApplication);
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private TrainerApplicationResponse mapToResponse(TrainerApplication application) {
        User user = application.getUser();

        TrainerApplicationResponse.UserBasicInfo userInfo = TrainerApplicationResponse.UserBasicInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .build();

        return TrainerApplicationResponse.builder()
                .id(application.getId())
                .userId(user.getId())
                .qualifications(application.getQualifications())
                .experienceDetails(application.getExperienceDetails())
                .documentUrls(application.getDocumentUrls())
                .status(application.getStatus())
                .adminFeedback(application.getAdminFeedback())
                .createdAt(Instant.from(application.getCreatedAt()))
                .updatedAt(Instant.from(application.getUpdatedAt()))
                .user(userInfo)
                .build();
    }

    private void sendApplicationResultEmail(TrainerApplication application) {
        User user = application.getUser();
        ApplicationStatus status = application.getStatus();

        if (status == ApplicationStatus.APPROVED) {
            // Email chúc mừng được duyệt
            String subject = "Chúc mừng! Bạn đã trở thành Personal Trainer";
            String content = String.format(
                    "Xin chúc mừng %s!<br><br>" +
                            "Đơn đăng ký trở thành Personal Trainer của bạn đã được phê duyệt.<br>" +
                            "Bây giờ bạn có thể tạo các khóa học và bắt đầu kiếm tiền trên nền tảng.<br><br>" +
                            "Chào mừng bạn đến với đội ngũ huấn luyện viên chuyên nghiệp!",
                    user.getFullName()
            );

            // TODO: Use email template
            // emailNotificationService.sendEmail(user, subject, content);

        } else if (status == ApplicationStatus.REJECTED) {
            // Email thông báo từ chối
            String subject = "Thông báo về đơn đăng ký Personal Trainer";
            String content = String.format(
                    "Xin chào %s,<br><br>" +
                            "Rất tiếc, đơn đăng ký trở thành Personal Trainer của bạn chưa được phê duyệt.<br><br>" +
                            "<strong>Lý do:</strong><br>%s<br><br>" +
                            "Bạn có thể hoàn thiện hồ sơ và gửi lại đơn đăng ký sau.",
                    user.getFullName(),
                    application.getAdminFeedback()
            );

            // TODO: Use email template
            // emailNotificationService.sendEmail(user, subject, content);
        }
    }

}

