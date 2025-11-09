package fa.training.fithub.controller;

import fa.training.fithub.dto.request.UpdateProfileRequest;
import fa.training.fithub.dto.response.ApiResponse;
import fa.training.fithub.dto.response.ProfileResponseDTO;
import fa.training.fithub.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    @GetMapping("/{id}")
    public ApiResponse<ProfileResponseDTO> getProfile(@PathVariable Long id) {
        ProfileResponseDTO response = profileService.getProfileUserById(id);
        return new ApiResponse<>(true,response,"Success");
    }
    @PutMapping("/{id}")
    public ApiResponse<ProfileResponseDTO> updateProfile(
            @PathVariable Long id,
            @RequestBody UpdateProfileRequest updateData) {

        ProfileResponseDTO response = profileService.UpdateProfile(updateData,id);
        return new ApiResponse<>(true ,response, "Update Success");
    }

}
