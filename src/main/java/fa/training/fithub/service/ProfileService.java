package fa.training.fithub.service;

import fa.training.fithub.dto.request.UpdateProfileRequest;
import fa.training.fithub.dto.response.ProfileResponseDTO;
import fa.training.fithub.entity.User;

public interface ProfileService {
    ProfileResponseDTO getProfileUserById(int id);
    ProfileResponseDTO UpdateProfile(UpdateProfileRequest updateProfile, int id);
}
