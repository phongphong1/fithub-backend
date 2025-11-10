package fa.training.fithub.controller;

import fa.training.fithub.dto.request.ProcessApplicationRequest;
import fa.training.fithub.dto.request.TrainerApplicationRequest;
import fa.training.fithub.dto.response.ApiResponse;
import fa.training.fithub.dto.response.TrainerApplicationResponse;
import fa.training.fithub.enums.ApplicationStatus;
import fa.training.fithub.service.TrainerApplicationService;
import fa.training.fithub.util.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trainer-applications")
@RequiredArgsConstructor
public class TrainerApplicationController {

    private final TrainerApplicationService trainerApplicationService;
    private final JwtService jwtService;

    /**
     * Submit trainer application (Gymer only)
     * POST /trainer-applications
     */
//    @PostMapping
//    public ResponseEntity<ApiResponse<TrainerApplicationResponse>> submitApplication(
//            @Valid @RequestBody TrainerApplicationRequest request,
//            @RequestHeader("Authorization") String authHeader
//    ) {
//        Long userId = jwtService.getUserIdFromAuthHeader(authHeader);
//
//        TrainerApplicationResponse response = trainerApplicationService.submitApplication(request, userId);
//
//        return new ResponseEntity<>(
//                ApiResponse.<TrainerApplicationResponse>builder()
//                        .success(true)
//                        .message("Application submitted successfully")
//                        .data(response)
//                        .build(),
//                HttpStatus.CREATED
//        );
//    }
//
//    /**
//     * Get my application status
//     * GET /trainer-applications/me
//     */
//    @GetMapping("/me")
//    public ResponseEntity<ApiResponse<TrainerApplicationResponse>> getMyApplication(
//            @RequestHeader("Authorization") String authHeader
//    ) {
//        Long userId = jwtService.getUserIdFromAuthHeader(authHeader);
//
//        TrainerApplicationResponse response = trainerApplicationService.getMyApplication(userId);
//
//        return ResponseEntity.ok(
//                ApiResponse.<TrainerApplicationResponse>builder()
//                        .success(true)
//                        .data(response)
//                        .build()
//        );
//    }

    /**
     * Get all applications (Admin only)
     * GET /trainer-applications?status=pending&page=0&limit=20
     * TEMP: Removed @PreAuthorize for testing
     */
    @GetMapping
    // @PreAuthorize("hasAnyAuthority('ADMIN', 'SYSTEM_ADMIN')") // TODO: Enable in production
    public ResponseEntity<ApiResponse<Page<TrainerApplicationResponse>>> getAllApplications(
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        // Limit max 100 items per page
        if (limit > 100) {
            limit = 100;
        }

        // Sort by created_at DESC (newest first)
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());

        Page<TrainerApplicationResponse> applications = trainerApplicationService.getAllApplications(status, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<TrainerApplicationResponse>>builder()
                        .success(true)
                        .data(applications)
                        .build()
        );
    }

    /**
     * Process application - Approve or Reject (Admin only)
     * PUT /trainer-applications/:application_id
     * TEMP: Removed @PreAuthorize for testing
     */
    @PutMapping("/{applicationId}")
    // @PreAuthorize("hasAnyAuthority('ADMIN', 'SYSTEM_ADMIN')") // TODO: Enable in production
    public ResponseEntity<ApiResponse<TrainerApplicationResponse>> processApplication(
            @PathVariable Long applicationId,
            @Valid @RequestBody ProcessApplicationRequest request
    ) {
        TrainerApplicationResponse response = trainerApplicationService.processApplication(applicationId, request);

        String message = request.getStatus() == ApplicationStatus.APPROVED
                ? "Application approved successfully"
                : "Application rejected";

        return ResponseEntity.ok(
                ApiResponse.<TrainerApplicationResponse>builder()
                        .success(true)
                        .message(message)
                        .data(response)
                        .build()
        );
    }

}

