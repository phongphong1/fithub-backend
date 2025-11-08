package fa.training.fithub.service.impl;

import fa.training.fithub.constants.MessageConstants;
import fa.training.fithub.dto.request.RegisterRequest;
import fa.training.fithub.dto.response.RegisterResponse;
import fa.training.fithub.entity.Token;
import fa.training.fithub.entity.User;
import fa.training.fithub.enums.Gender;
import fa.training.fithub.enums.TokenType;
import fa.training.fithub.exception.DuplicateEmailException;
import fa.training.fithub.exception.DuplicateUsernameException;
import fa.training.fithub.repository.TokenRepository;
import fa.training.fithub.repository.UserRepository;
import fa.training.fithub.service.SystemConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Service Implementation Tests")
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private SystemConfigService systemConfigService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailNotificationService emailNotificationService;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private User mockUser;

    @BeforeEach
    void setUp() {
        // Set up frontend URL using reflection
        ReflectionTestUtils.setField(authService, "frontendUrl", "http://localhost:3000");

        // Set up register request
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("Password@123");
        registerRequest.setFullName("Test User");
        registerRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        registerRequest.setGender(Gender.MALE);

        // Set up mock user
        mockUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .passwordHash("encodedPassword")
                .fullName("Test User")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender(Gender.MALE)
                .build();
    }

    @Test
    @DisplayName("Should register user successfully")
    void testRegisterSuccess() {
        // Arrange
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(systemConfigService.getInteger(anyString(), anyInt())).thenReturn(24);
        when(tokenRepository.save(any(Token.class))).thenReturn(new Token());

        // Act
        RegisterResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals(mockUser.getId(), response.getUserId());
        assertEquals(mockUser.getUsername(), response.getUsername());
        assertEquals(mockUser.getEmail(), response.getEmail());

        // Verify interactions
        verify(userRepository, times(1)).existsByUsername(registerRequest.getUsername());
        verify(userRepository, times(1)).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder, times(1)).encode(registerRequest.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
        verify(tokenRepository, times(1)).save(any(Token.class));
        verify(emailNotificationService, times(1)).sendEmailWithTemplate(
                any(User.class),
                eq(MessageConstants.Email.EMAIL_VERIFICATION_SUBJECT),
                eq("email-verification"),
                anyMap());
    }

    @Test
    @DisplayName("Should throw DuplicateUsernameException when username already exists")
    void testRegisterWithDuplicateUsername() {
        // Arrange
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        // Act & Assert
        DuplicateUsernameException exception = assertThrows(
                DuplicateUsernameException.class,
                () -> authService.register(registerRequest));

        assertEquals(MessageConstants.Validation.USERNAME_ALREADY_EXISTS, exception.getMessage());

        // Verify that repository methods were called correctly
        verify(userRepository, times(1)).existsByUsername(registerRequest.getUsername());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(tokenRepository, never()).save(any(Token.class));
        verify(emailNotificationService, never()).sendEmailWithTemplate(
                any(User.class),
                anyString(),
                anyString(),
                anyMap());
    }

    @Test
    @DisplayName("Should throw DuplicateEmailException when email already exists")
    void testRegisterWithDuplicateEmail() {
        // Arrange
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // Act & Assert
        DuplicateEmailException exception = assertThrows(
                DuplicateEmailException.class,
                () -> authService.register(registerRequest));

        assertEquals(MessageConstants.Validation.EMAIL_ALREADY_EXISTS, exception.getMessage());

        // Verify that repository methods were called correctly
        verify(userRepository, times(1)).existsByUsername(registerRequest.getUsername());
        verify(userRepository, times(1)).existsByEmail(registerRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
        verify(tokenRepository, never()).save(any(Token.class));
        verify(emailNotificationService, never()).sendEmailWithTemplate(
                any(User.class),
                anyString(),
                anyString(),
                anyMap());
    }

    @Test
    @DisplayName("Should create user with correct data")
    void testRegisterCreatesUserWithCorrectData() {
        // Arrange
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(systemConfigService.getInteger(anyString(), anyInt())).thenReturn(24);
        when(tokenRepository.save(any(Token.class))).thenReturn(new Token());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Act
        authService.register(registerRequest);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertEquals(registerRequest.getUsername(), capturedUser.getUsername());
        assertEquals(registerRequest.getEmail(), capturedUser.getEmail());
        assertEquals("encodedPassword", capturedUser.getPasswordHash());
        assertEquals(registerRequest.getFullName(), capturedUser.getFullName());
        assertEquals(registerRequest.getDateOfBirth(), capturedUser.getDateOfBirth());
        assertEquals(registerRequest.getGender(), capturedUser.getGender());
    }

    @Test
    @DisplayName("Should create verification token with correct type")
    void testRegisterCreatesVerificationToken() {
        // Arrange
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(systemConfigService.getInteger(anyString(), anyInt())).thenReturn(24);
        when(tokenRepository.save(any(Token.class))).thenReturn(new Token());

        ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);

        // Act
        authService.register(registerRequest);

        // Assert
        verify(tokenRepository).save(tokenCaptor.capture());
        Token capturedToken = tokenCaptor.getValue();

        assertEquals(TokenType.EMAIL_VERIFICATION, capturedToken.getType());
        assertNotNull(capturedToken.getToken());
        assertTrue(capturedToken.getIsActive());
        assertNotNull(capturedToken.getExpiresAt());
        assertEquals(mockUser, capturedToken.getUser());
    }

    @Test
    @DisplayName("Should send verification email with correct data")
    void testRegisterSendsVerificationEmail() {
        // Arrange
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(systemConfigService.getInteger(anyString(), anyInt())).thenReturn(24);
        when(tokenRepository.save(any(Token.class))).thenReturn(new Token());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> variablesCaptor = ArgumentCaptor.forClass(Map.class);

        // Act
        authService.register(registerRequest);

        // Assert
        verify(emailNotificationService).sendEmailWithTemplate(
                eq(mockUser),
                eq(MessageConstants.Email.EMAIL_VERIFICATION_SUBJECT),
                eq("email-verification"),
                variablesCaptor.capture());

        Map<String, Object> capturedVariables = variablesCaptor.getValue();
        assertEquals(mockUser.getFullName(), capturedVariables.get("fullname"));
        assertEquals(MessageConstants.Auth.REGISTER_THANK_YOU, capturedVariables.get("action_reason"));
        assertNotNull(capturedVariables.get("verification_link"));
        assertTrue(capturedVariables.get("verification_link").toString()
                .contains("http://localhost:3000/verify-email?token="));
        assertEquals("24", capturedVariables.get("expiry_time"));
    }

    @Test
    @DisplayName("Should use default token expiry when config is not available")
    void testRegisterUsesDefaultTokenExpiry() {
        // Arrange
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(systemConfigService.getInteger(anyString(), anyInt())).thenReturn(24); // Returns default
        when(tokenRepository.save(any(Token.class))).thenReturn(new Token());

        // Act
        authService.register(registerRequest);

        // Assert
        verify(systemConfigService, times(1)).getInteger("email_verification_token_expiry_hours", 24);
    }

    @Test
    @DisplayName("Should encode password before saving user")
    void testRegisterEncodesPassword() {
        // Arrange
        String rawPassword = "Password@123";
        String encodedPassword = "encodedPassword123";

        registerRequest.setPassword(rawPassword);

        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(systemConfigService.getInteger(anyString(), anyInt())).thenReturn(24);
        when(tokenRepository.save(any(Token.class))).thenReturn(new Token());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Act
        authService.register(registerRequest);

        // Assert
        verify(passwordEncoder, times(1)).encode(rawPassword);
        verify(userRepository).save(userCaptor.capture());
        assertEquals(encodedPassword, userCaptor.getValue().getPasswordHash());
    }
}
