package fa.training.fithub.controller;

import fa.training.fithub.dto.response.ProfileResponseDTO;
import fa.training.fithub.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    @GetMapping("/")
    public ResponseEntity<ProfileResponseDTO> getProfile() {
        System.out.println("fnsjdf");
        ProfileResponseDTO response = profileService.getProfileUserById(1);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
