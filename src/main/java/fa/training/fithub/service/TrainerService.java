package fa.training.fithub.service;

import fa.training.fithub.dto.response.ApiResponse;

public interface TrainerService {
    ApiResponse<Object> sendApplication();
}
