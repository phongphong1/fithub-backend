package fa.training.fithub.service;

import fa.training.fithub.dto.response.NewAccessTokenResponseDTO;

public interface RefreshTokenService {
    NewAccessTokenResponseDTO getNewAccessToken(String refreshToken);
}
