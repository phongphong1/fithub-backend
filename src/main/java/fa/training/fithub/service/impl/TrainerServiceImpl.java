package fa.training.fithub.service.impl;

import fa.training.fithub.dto.request.TrainerApplicationRequest;
import fa.training.fithub.dto.response.ApiResponse;
import fa.training.fithub.entity.TrainerApplication;
import fa.training.fithub.entity.User;
import fa.training.fithub.enums.ApplicationStatus;
import fa.training.fithub.exception.BadRequestException;
import fa.training.fithub.exception.UnauthorizedException;
import fa.training.fithub.exception.UserNotFoundException;
import fa.training.fithub.repository.TrainerApplicationRepository;
import fa.training.fithub.repository.UserRepository;
import fa.training.fithub.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of TrainerService
 */
@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private final TrainerApplicationRepository trainerApplicationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ApiResponse<Object> sendApplication(TrainerApplicationRequest request) {
        logger.info("Processing trainer application submission");

        // Get authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User must be authenticated to submit trainer application");
        }

        String username = ((User)authentication.getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        // Check if user already has a pending or approved application
        trainerApplicationRepository.findByUser(user).ifPresent(existingApp -> {
            if (existingApp.getStatus() == ApplicationStatus.PENDING) {
                throw new BadRequestException("You already have a pending trainer application");
            }
            if (existingApp.getStatus() == ApplicationStatus.APPROVED) {
                throw new BadRequestException("You are already approved as a trainer");
            }
        });

        // Validate request
        if (request == null) {
            throw new BadRequestException("Application request cannot be null");
        }

        if (request.getQualifications() == null || request.getQualifications().trim().isEmpty()) {
            throw new BadRequestException("Qualifications are required");
        }

        if (request.getExperience_details() == null || request.getExperience_details().trim().isEmpty()) {
            throw new BadRequestException("Experience details are required");
        }

        if (request.getCertificateUrls() == null || request.getCertificateUrls().isEmpty()) {
            throw new BadRequestException("At least one certificate is required");
        }

        // Create trainer application entity
        TrainerApplication application = new TrainerApplication();
        application.setUser(user);
        application.setQualifications(request.getQualifications().trim());
        application.setExperienceDetails(request.getExperience_details().trim());

        // Store certificate URLs in JSON format
        Map<String, Object> documentUrls = new HashMap<>();
        documentUrls.put("certificates", request.getCertificateUrls());
        application.setDocumentUrls(documentUrls);

        application.setStatus(ApplicationStatus.PENDING);

        // Save to database
        TrainerApplication savedApplication = trainerApplicationRepository.save(application);

        logger.info("Trainer application submitted successfully with ID: {} by user: {}",
                savedApplication.getId(), username);

        return ApiResponse.builder()
                .success(true)
                .data(savedApplication)
                .message("Trainer application submitted successfully. We will review it soon.")
                .build();
    }
}
