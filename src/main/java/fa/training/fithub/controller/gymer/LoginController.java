package fa.training.fithub.controller.gymer;

import fa.training.fithub.dto.request.LoginRequestDTO;
import fa.training.fithub.dto.response.ApiResponse;
import fa.training.fithub.dto.response.LoginResponseDTO;
import fa.training.fithub.service.LoginService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@RequestBody LoginRequestDTO loginRequestDTO) {

        LoginResponseDTO login = loginService.login(loginRequestDTO);

        return ResponseEntity.ok(ApiResponse.<LoginResponseDTO>builder().success(true).data(login).message("Login successful").build());
    }

}
