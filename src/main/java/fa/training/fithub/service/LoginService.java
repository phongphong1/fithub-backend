package fa.training.fithub.service;

import fa.training.fithub.dto.request.LoginRequestDTO;
import fa.training.fithub.dto.response.LoginResponseDTO;

public interface LoginService {
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
}
