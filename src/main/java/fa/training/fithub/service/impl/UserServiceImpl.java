package fa.training.fithub.service.impl;

import fa.training.fithub.dto.response.UserResponseDTO;
import fa.training.fithub.dto.response.UserResponseFullFieldDTO;
import fa.training.fithub.entity.User;
import fa.training.fithub.exception.CustomException;
import fa.training.fithub.exception.UserNotFoundException;
import fa.training.fithub.repository.UserRepository;
import fa.training.fithub.service.UserService;
import fa.training.fithub.util.JwtService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;

    @Override
    public UserResponseFullFieldDTO getUserByRefreshToken(String refreshToken) {
        // 1️⃣ Giải mã token và lấy username
        String username = jwtService.validateTokenAndGetUsername(refreshToken)
                .orElseThrow(() -> new CustomException("Invalid or expired refresh token", HttpStatus.UNAUTHORIZED));

        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found for token"));

        UserResponseFullFieldDTO userResponseFullFieldDTO = modelMapper.map(user, UserResponseFullFieldDTO.class);

        return userResponseFullFieldDTO;
    }
}
