package fa.training.fithub.constants;

/**
 * Class containing all message constants used in the application.
 * Helps easily manage and reuse messages.
 */
public final class MessageConstants {

    private MessageConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // ==================== AUTH MESSAGES ====================
    public static final class Auth {
        private Auth() {
        }

        public static final String REGISTER_SUCCESS = "Registration successful! Please check your email to verify your account.";
        public static final String REGISTER_THANK_YOU = "Thank you for registering. Please click the button below to complete the process.";
    }

    // ==================== VALIDATION MESSAGES ====================
    public static final class Validation {
        private Validation() {
        }

        public static final String INVALID_INPUT_DATA = "Invalid input data";
        public static final String USERNAME_ALREADY_EXISTS = "Username already exists!";
        public static final String EMAIL_ALREADY_EXISTS = "Email already exists!";
    }

    // ==================== EMAIL MESSAGES ====================
    public static final class Email {
        private Email() {
        }

        public static final String EMAIL_VERIFICATION_SUBJECT = "Email Address Verification";
        public static final String EMAIL_VERIFICATION_SUCCESS = "Email verification successful! You can now log in.";
        public static final String EMAIL_ALREADY_VERIFIED = "Email has already been verified!";
        public static final String EMAIL_ALREADY_VERIFIED_CAN_LOGIN = "Email has already been verified! You can log in.";
        public static final String EMAIL_RESENT_SUCCESS = "Verification email has been resent! Please check your inbox.";
    }

    // ==================== TOKEN MESSAGES ====================
    public static final class Token {
        private Token() {
        }

        public static final String INVALID_TOKEN = "Invalid activation link!";
        public static final String EXPIRED_TOKEN = "Activation link has expired!";
    }

    // ==================== USER MESSAGES ====================
    public static final class User {
        private User() {
        }

        public static final String USER_NOT_FOUND_BY_EMAIL = "No account found with this email!";
        public static final String USER_NOT_FOUND = "User not found!";
    }

    // ==================== GENERAL MESSAGES ====================
    public static final class General {
        private General() {
        }

        public static final String OPERATION_SUCCESS = "Operation successful!";
        public static final String OPERATION_FAILED = "Operation failed!";
        public static final String INTERNAL_SERVER_ERROR = "A system error occurred. Please try again later!";
    }


}
