package fa.training.fithub.service.impl;

import fa.training.fithub.constants.MessageConstants;
import fa.training.fithub.dto.request.ResendVerificationEmailRequest;
import fa.training.fithub.dto.request.RegisterRequest;
import fa.training.fithub.dto.response.ApiResponse;
import fa.training.fithub.dto.response.RegisterResponse;
import fa.training.fithub.entity.Token;
import fa.training.fithub.entity.User;
import fa.training.fithub.enums.TokenType;
import fa.training.fithub.enums.UserStatus;
import fa.training.fithub.exception.AlreadyVerifiedException;
import fa.training.fithub.exception.DuplicateEmailException;
import fa.training.fithub.exception.DuplicateUsernameException;
import fa.training.fithub.exception.ExpiredTokenException;
import fa.training.fithub.exception.InvalidTokenException;
import fa.training.fithub.exception.UserNotFoundException;
import fa.training.fithub.repository.TokenRepository;
import fa.training.fithub.repository.UserRepository;
import fa.training.fithub.service.AuthService;
import fa.training.fithub.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final SystemConfigService systemConfigService;
    private final PasswordEncoder passwordEncoder;
    private final EmailNotificationService emailNotificationService;

    private final static String EMAIL_TOKEN_EXPIRE_KEY = "email_verification_token_expiry_hours";
    private final static int EMAIL_TOKEN_EXPIRE_DEFAULT = 24;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public Token generateToken(String username) {
        return null;
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {

        // Kiểm tra username và email đã tồn tại
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new DuplicateUsernameException(MessageConstants.Validation.USERNAME_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateEmailException(MessageConstants.Validation.EMAIL_ALREADY_EXISTS);
        }

        // Tạo user mới
        User user = User.builder()
                .username(registerRequest.getUsername())
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .fullName(registerRequest.getFullName())
                .dateOfBirth(registerRequest.getDateOfBirth())
                .gender(registerRequest.getGender())
                .build();
        User savedUser = userRepository.save(user);

        createAndSendVerificationEmail(savedUser);

        return RegisterResponse.builder()
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .build();
    }

    @Transactional
    public void createAndSendVerificationEmail(User user) {
        // Tạo token để xác thực email
        String tokenValue = UUID.randomUUID().toString();
        int tokenExpiryHours = systemConfigService.getInteger(EMAIL_TOKEN_EXPIRE_KEY, EMAIL_TOKEN_EXPIRE_DEFAULT);

        LocalDateTime nowUtc = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime expiryUtc = nowUtc.plusHours(tokenExpiryHours);

        Token verificationToken = Token.builder()
                .token(tokenValue)
                .user(user)
                .type(TokenType.EMAIL_VERIFICATION)
                .isActive(true)
                .createdAt(nowUtc)
                .expiresAt(expiryUtc)
                .build();
        tokenRepository.save(verificationToken);

        // Gửi email xác thực
        String verificationLink = frontendUrl + "/verify-email?token=" + tokenValue;
        Map<String, Object> variables = new HashMap<>();
        variables.put("fullname", user.getFullName());
        variables.put("action_reason", MessageConstants.Auth.REGISTER_THANK_YOU);
        variables.put("verification_link", verificationLink);
        variables.put("expiry_time", String.valueOf(tokenExpiryHours));

        emailNotificationService.sendEmailWithTemplate(
                user,
                MessageConstants.Email.EMAIL_VERIFICATION_SUBJECT,
                "email-verification",
                variables);
    }

    @Override
    @Transactional
    public ApiResponse<Object> verifyEmail(String tokenValue) {
        Token token = tokenRepository.findByTokenAndType(tokenValue, TokenType.EMAIL_VERIFICATION)
                .orElseThrow(() -> new InvalidTokenException(MessageConstants.Token.INVALID_TOKEN));
        User user = token.getUser();

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new AlreadyVerifiedException(MessageConstants.Email.EMAIL_ALREADY_VERIFIED);
        }

        if (!token.getIsActive()) {
            throw new InvalidTokenException(MessageConstants.Token.INVALID_TOKEN);
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
            throw new ExpiredTokenException("Link kích hoạt đã hết hạn!");
        }

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        token.setIsActive(false);
        tokenRepository.save(token);

        return ApiResponse.<Object>builder()
                .success(true)
                .message(MessageConstants.Email.EMAIL_VERIFICATION_SUCCESS)
                .data(null)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<Object> resendVerificationEmail(ResendVerificationEmailRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(MessageConstants.User.USER_NOT_FOUND_BY_EMAIL));

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new AlreadyVerifiedException(MessageConstants.Email.EMAIL_ALREADY_VERIFIED_CAN_LOGIN);
        }

        tokenRepository.findAllByUserAndType(user, TokenType.EMAIL_VERIFICATION)
                .forEach(token -> {
                    token.setIsActive(false);
                    tokenRepository.save(token);
                });

        createAndSendVerificationEmail(user);

        return ApiResponse.<Object>builder()
                .success(true)
                .message(MessageConstants.Email.EMAIL_RESENT_SUCCESS)
                .data(null)
                .build();
    }

}
