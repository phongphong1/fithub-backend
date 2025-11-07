package fa.training.fithub.service.impl;

import fa.training.fithub.dto.request.LoginRequestDTO;
import fa.training.fithub.dto.response.LoginResponseDTO;
import fa.training.fithub.dto.response.TokensResponseDTO;
import fa.training.fithub.dto.response.UserResponseDTO;
import fa.training.fithub.entity.Token;
import fa.training.fithub.entity.User;
import fa.training.fithub.enums.TokenType;
import fa.training.fithub.enums.UserStatus;
import fa.training.fithub.exception.BadRequestException;
import fa.training.fithub.exception.ForbiddenException;
import fa.training.fithub.repository.TokenRepository;
import fa.training.fithub.repository.UserRepository;
import fa.training.fithub.service.LoginService;
import fa.training.fithub.util.JwtService;
import fa.training.fithub.util.StringUtilsBuild;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final ModelMapper modelMapper;

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {

        // Validate
        if (!StringUtilsBuild.isNotBlank(loginRequestDTO.getEmailOrUsername()) && !StringUtilsBuild.isNotBlank(loginRequestDTO.getPassword())) {
            throw new BadRequestException("Username And Password must not be blank");
        }

        if (!StringUtilsBuild.isNotBlank(loginRequestDTO.getEmailOrUsername())) {
            throw new BadRequestException("Username must not be blank");
        }

        if (!StringUtilsBuild.isNotBlank(loginRequestDTO.getPassword())) {
            throw new BadRequestException("Password must not be blank");
        }

        // Fetch user
        String identifier = loginRequestDTO.getEmailOrUsername();
        User user;
        if (identifier.contains("@")) {
            user = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new BadRequestException(
                            String.format("User not found with email: '%s'", identifier)
                    ));
        } else {
            user = userRepository.findByUsername(identifier)
                    .orElseThrow(() -> new BadRequestException(
                            String.format("User not found with username: '%s'", identifier)
                    ));
        }

        // Check password
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException(
                    String.format("Invalid password: '%s'", identifier)
            );
        }

        // Check status account
        if (user.getStatus() == UserStatus.BANNED || user.getStatus() == UserStatus.INACTIVE) {
            throw new ForbiddenException(
                    String.format("Account with identifier '%s' is %s", identifier, user.getStatus())
            );
        }

        // Update last login time (LocalDateTime)
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Just login 1 position
        tokenRepository.findAllByUserAndIsActiveTrue(user).forEach(oldToken -> {
            oldToken.setIsActive(false);
            tokenRepository.save(oldToken);
        });

        // Creating token
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user, loginRequestDTO.isRememberMe());

        long refreshExpSeconds = jwtService.getRefreshExpSeconds(loginRequestDTO.isRememberMe());

        // Save refresh token to database (LocalDateTime)
        LocalDateTime now = LocalDateTime.now();

        Token token = Token.builder()
                .user(user)
                .type(TokenType.REFRESH)
                .token(refreshToken)
                .isActive(true)
                .createdAt(now)
                .expiresAt(now.plusSeconds(refreshExpSeconds))
                .build();
        tokenRepository.save(token);

        // Using ModelMapper converter between 2 object
        UserResponseDTO userResponseDTO = modelMapper.map(user, UserResponseDTO.class);

        return LoginResponseDTO.builder()
                .user(userResponseDTO)
                .tokens(TokensResponseDTO.builder()
                        .access_token(accessToken)
                        .refresh_token(refreshToken)
                        .expires_in(jwtService.getAccessExpSeconds())
                        .build())
                .build();
    }
}
