package fa.training.fithub.controller.trainer;

import fa.training.fithub.dto.request.TrainerApplicationRequest;
import fa.training.fithub.dto.response.ApiResponse;
import fa.training.fithub.service.TrainerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for trainer-related operations
 */
@RestController
@RequestMapping("/trainer")
@RequiredArgsConstructor
public class TrainerController {

    private static final Logger logger = LoggerFactory.getLogger(TrainerController.class);

    private final TrainerService trainerService;

    /**
     * Submit a trainer application
     * 
     * @param request the trainer application request
     * @return ApiResponse with application details
     */
    @PostMapping("/application")
    public ResponseEntity<ApiResponse<Object>> submitApplication(
            @Valid @RequestBody TrainerApplicationRequest request) {

        logger.info("Received trainer application submission");

        ApiResponse<Object> response = trainerService.sendApplication(request);

        return ResponseEntity.ok(response);
    }
}
