package fa.training.fithub.dto.request;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String emailOrUsername;
    private String password;
    private boolean rememberMe;
}
