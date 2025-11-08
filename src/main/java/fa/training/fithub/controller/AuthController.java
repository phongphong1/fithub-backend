package fa.training.fithub.controller;

import fa.training.fithub.dto.ResendVerificationEmailRequest;
import fa.training.fithub.constants.MessageConstants;
import fa.training.fithub.dto.request.ResendVerificationEmailRequest;
import fa.training.fithub.dto.request.RegisterRequest;
import fa.training.fithub.dto.response.ApiResponse;
import fa.training.fithub.dto.response.RegisterResponse;
import fa.training.fithub.dto.response.*;
import fa.training.fithub.service.AuthService;
import fa.training.fithub.service.RefreshTokenService;
import fa.training.fithub.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final RefreshTokenService refreshTokenService;

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest registerRequest) {

        RegisterResponse registerResponse = authService.register(registerRequest);

        ApiResponse<RegisterResponse> response = ApiResponse.<RegisterResponse>builder()
                .success(true)
                .message(MessageConstants.Auth.REGISTER_SUCCESS)
                .data(registerResponse)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<Object>> verifyEmail(@RequestParam String token) {
        ApiResponse<Object> response = authService.verifyEmail(token);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/resend-verification-email")
    public ResponseEntity<ApiResponse<Object>> resendVerificationEmail(
            @Valid @RequestBody ResendVerificationEmailRequest request) {
        ApiResponse<Object> response = authService.resendVerificationEmail(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/check-one-position/{refreshToken}/{accessToken}")
    public ResponseEntity<ApiResponse<?>> checkOnePosition(@PathVariable String refreshToken,
                                                           @PathVariable String accessToken) {
        authService.checkOnePosition(refreshToken, accessToken);
        ApiResponse<Object> response = ApiResponse.builder()
                .success(true)
                .data(refreshToken)
                .message("Check one position successful")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<NewAccessTokenResponseDTO>> newAccessToken(
            @RequestBody Map<String, String> requestBody) {

        String refreshToken = requestBody.get("refresh_token");
        NewAccessTokenResponseDTO newAccessTokenResponseDTO = refreshTokenService.getNewAccessToken(refreshToken);

        return ResponseEntity.ok(
                ApiResponse.<NewAccessTokenResponseDTO>builder()
                        .success(true)
                        .data(newAccessTokenResponseDTO)
                        .message("Get new access token successful")
                        .build()
        );
    }

    @PostMapping("/me-from-refresh")
    public ResponseEntity<ApiResponse<?>> getUserFromRefreshToken(@RequestBody Map<String, String> requestBody) {
        String refreshToken = requestBody.get("refresh_token");
        UserResponseFullFieldDTO userResponseFullFieldDTO = userService.getUserByRefreshToken(refreshToken);
        ApiResponse<Object> response = ApiResponse.builder()
                .success(true)
                .data(userResponseFullFieldDTO)
                .message("Get user from refresh token successful")
                .build();
        return ResponseEntity.ok(response);
    }

}
