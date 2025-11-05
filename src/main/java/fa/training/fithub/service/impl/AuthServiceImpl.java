package fa.training.fithub.service.impl;

import fa.training.fithub.dto.RegisterRequest;
import fa.training.fithub.dto.RegisterResponse;
import fa.training.fithub.entity.Token;
import fa.training.fithub.entity.User;
import fa.training.fithub.enums.TokenType;
import fa.training.fithub.exception.DuplicateEmailException;
import fa.training.fithub.exception.DuplicateUsernameException;
import fa.training.fithub.repository.TokenRepository;
import fa.training.fithub.repository.UserRepository;
import fa.training.fithub.service.AuthService;
import fa.training.fithub.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
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

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public Token generateToken(String username) {
        return null;
    }

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {

        // Kiểm tra username và email đã tồn tại
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new DuplicateUsernameException("Username đã tồn tại!");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateEmailException("Email đã tồn tại!");
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

        // Tạo token để xác thực email
        String tokenValue = UUID.randomUUID().toString();
        int tokenExpiryHours = systemConfigService.getInteger(EMAIL_TOKEN_EXPIRE_KEY, EMAIL_TOKEN_EXPIRE_DEFAULT);
        Token verificationToken = Token.builder()
                .token(tokenValue)
                .user(savedUser)
                .expiresAt(Instant.now().plusSeconds(tokenExpiryHours))
                .type(TokenType.email_verification)
                .build();
        tokenRepository.save(verificationToken);

        // Gửi email xác thực
        String verificationLink = baseUrl + "/api/auth/verify-email?token=" + tokenValue;
        Map<String, Object> variables = new HashMap<>();
        variables.put("fullname", savedUser.getFullName());
        variables.put("action_reason", "Cảm ơn bạn đã đăng ký tài khoản. Vui lòng nhấp vào nút bên dưới để hoàn tất.");
        variables.put("verification_link", verificationLink);
        variables.put("expiry_time", String.valueOf(tokenExpiryHours));
        emailNotificationService.sendEmailWithTemplate(
                savedUser,
                "Xác thực địa chỉ email",
                "email-verification",
                variables);

        return RegisterResponse.builder()
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .build();
    }
}
