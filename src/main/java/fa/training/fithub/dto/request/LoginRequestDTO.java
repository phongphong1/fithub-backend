package fa.training.fithub.dto.request;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String username;   // có thể cho phép email luôn (xử lý ở service)
    private String password;
}
