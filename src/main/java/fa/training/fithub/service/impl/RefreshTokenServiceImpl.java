package fa.training.fithub.service.impl;

import fa.training.fithub.dto.response.NewAccessTokenResponseDTO;
import fa.training.fithub.entity.Token;
import fa.training.fithub.entity.User;
import fa.training.fithub.enums.TokenType;
import fa.training.fithub.exception.UnauthorizedException;
import fa.training.fithub.repository.TokenRepository;
import fa.training.fithub.repository.UserRepository;
import fa.training.fithub.service.RefreshTokenService;
import fa.training.fithub.util.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    private static final long ACCESS_TTL_SECONDS = 3600;

    @Override
    public NewAccessTokenResponseDTO getNewAccessToken(String refreshToken) {
        // Tìm refresh token còn active, đúng type
        Token stored = tokenRepository
                .findByTokenAndType(refreshToken, TokenType.REFRESH)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        // Check hết hạn refresh token
        if (stored.getExpiresAt().atZone(ZoneId.systemDefault()).toInstant().isBefore(Instant.now())) {
            stored.setIsActive(false);
            tokenRepository.save(stored);
            throw new UnauthorizedException("Refresh token expired");
        }

        // Lấy user
        String claims = jwtService.getUsernameFromToken(refreshToken);
        if (claims == null) {
            throw new UnauthorizedException("User not found for refresh token");
        }
        User user = userRepository.findByUsername(claims)
                .orElseThrow(() -> new UnauthorizedException("not found username"));

        // Sinh access token mới
        String newAccess = jwtService.generateAccessToken(user);

        // Tính thời gian còn lại của refresh (giây)
        long refreshSecondsLeft = Duration.between(
                Instant.now(),
                stored.getExpiresAt().atZone(ZoneId.systemDefault()).toInstant()
        ).getSeconds();
        if (refreshSecondsLeft < 0) refreshSecondsLeft = 0;

        // Trả về DTO
        return NewAccessTokenResponseDTO.builder()
                .access_token(newAccess)
                .expires_in(ACCESS_TTL_SECONDS)
                .refresh_token(refreshToken)           // giữ nguyên
                .refresh_expires_in(refreshSecondsLeft) // còn lại bao nhiêu giây
                .build();
    }

}
