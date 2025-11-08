package fa.training.fithub.service;

import fa.training.fithub.dto.response.UserResponseFullFieldDTO;
import org.springframework.stereotype.Service;

public interface UserService {

    UserResponseFullFieldDTO getUserByRefreshToken(String refreshToken);
}
