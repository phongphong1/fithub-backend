package fa.training.fithub.service;

import fa.training.fithub.dto.request.RegisterRequest;
import fa.training.fithub.dto.response.RegisterResponse;
import fa.training.fithub.entity.Token;

public interface AuthService {
    Token generateToken(String username);
    RegisterResponse register(RegisterRequest registerRequest);
}
