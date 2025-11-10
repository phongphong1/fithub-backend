package fa.training.fithub.service;

import fa.training.fithub.dto.request.ProcessApplicationRequest;
import fa.training.fithub.dto.request.TrainerApplicationRequest;
import fa.training.fithub.dto.response.TrainerApplicationResponse;
import fa.training.fithub.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TrainerApplicationService {

    /**
     * Submit trainer application (for Gymer)
     * @param request Application request data
     * @param userId Current user ID
     * @return Created application
     */
    TrainerApplicationResponse submitApplication(TrainerApplicationRequest request, Long userId);

    /**
     * Get current user's application
     * @param userId Current user ID
     * @return Application if exists
     */
    TrainerApplicationResponse getMyApplication(Long userId);

    /**
     * Get all applications (Admin only)
     * @param status Filter by status (optional)
     * @param pageable Pagination info
     * @return Page of applications
     */
    Page<TrainerApplicationResponse> getAllApplications(ApplicationStatus status, Pageable pageable);

    /**
     * Process application - Approve or Reject (Admin only)
     * @param applicationId Application ID
     * @param request Process request (status + feedback)
     * @return Updated application
     */
    TrainerApplicationResponse processApplication(Long applicationId, ProcessApplicationRequest request);

}

