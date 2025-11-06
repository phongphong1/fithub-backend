package fa.training.fithub.controller;

import fa.training.fithub.dto.ResendVerificationEmailRequest;
import fa.training.fithub.dto.request.RegisterRequest;
import fa.training.fithub.dto.response.ApiResponse;
import fa.training.fithub.dto.response.RegisterResponse;
import fa.training.fithub.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest registerRequest) {

        RegisterResponse registerResponse = authService.register(registerRequest);

        ApiResponse<RegisterResponse> response = ApiResponse.<RegisterResponse>builder()
                .success(true)
                .message("Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.")
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

}
