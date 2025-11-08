package fa.training.fithub.service;

import fa.training.fithub.dto.request.TrainerApplicationRequest;
import fa.training.fithub.dto.response.ApiResponse;

/**
 * Service interface for trainer-related operations
 */
public interface TrainerService {

    /**
     * Submit a trainer application
     * 
     * @param request the trainer application request containing qualifications,
     *                experience, and certificates
     * @return ApiResponse with the created application
     */
    ApiResponse<Object> sendApplication(TrainerApplicationRequest request);
}
