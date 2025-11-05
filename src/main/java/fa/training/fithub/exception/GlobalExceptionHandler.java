package fa.training.fithub.exception;

import fa.training.fithub.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
                        MethodArgumentNotValidException ex) {
                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                                .success(false)
                                .message("Dữ liệu đầu vào không hợp lệ")
                                .data(errors)
                                .build();
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(DuplicateUsernameException.class)
        @ResponseStatus(HttpStatus.CONFLICT)
        public ResponseEntity<ApiResponse<Object>> handleDuplicateUsername(DuplicateUsernameException ex) {
                ApiResponse<Object> response = ApiResponse.builder()
                                .success(false)
                                .message(ex.getMessage())
                                .data(null)
                                .build();
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        @ExceptionHandler(DuplicateEmailException.class)
        @ResponseStatus(HttpStatus.CONFLICT)
        public ResponseEntity<ApiResponse<Object>> handleDuplicateEmail(DuplicateEmailException ex) {
                ApiResponse<Object> response = ApiResponse.builder()
                                .success(false)
                                .message(ex.getMessage())
                                .data(null)
                                .build();
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

}
