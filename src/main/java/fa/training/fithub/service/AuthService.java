package fa.training.fithub.service;

import fa.training.fithub.dto.ApiResponse;
import fa.training.fithub.dto.RegisterRequest;
import fa.training.fithub.dto.RegisterResponse;
import fa.training.fithub.dto.ResendVerificationEmailRequest;
import fa.training.fithub.entity.Token;

public interface AuthService {
    Token generateToken(String username);

    RegisterResponse register(RegisterRequest registerRequest);

    ApiResponse<Object> verifyEmail(String token);

    ApiResponse<Object> resendVerificationEmail(ResendVerificationEmailRequest request);
}
