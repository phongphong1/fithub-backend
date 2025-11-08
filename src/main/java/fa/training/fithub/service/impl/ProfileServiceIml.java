package fa.training.fithub.service.impl;

import fa.training.fithub.dto.request.UpdateProfileRequest;
import fa.training.fithub.dto.response.ProfileResponseDTO;
import fa.training.fithub.entity.User;
import fa.training.fithub.exception.BadRequestException;
import fa.training.fithub.repository.UserRepository;
import fa.training.fithub.service.ProfileService;
import fa.training.fithub.util.JwtService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileServiceIml implements ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final ModelMapper modelMapper;

    @Override
    public ProfileResponseDTO getProfileUserById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("User not found"));
        return modelMapper.map(user, ProfileResponseDTO.class);
    }
    @Override
    public ProfileResponseDTO UpdateProfile(UpdateProfileRequest updateProfile, int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if(updateProfile.getFullName() != null) {
            user.setFullName(updateProfile.getFullName());
        }
        if(updateProfile.getGender() != null) {
            user.setGender(updateProfile.getGender());
        }
        if(updateProfile.getDateOfBirth() != null) {
            user.setDateOfBirth(updateProfile.getDateOfBirth());
        }
        if(updateProfile.getCoverUrl() != null) {
            user.setCoverUrl(updateProfile.getCoverUrl());
        }
        if(updateProfile.getAvatarUrl() != null) {
            user.setAvatarUrl(updateProfile.getAvatarUrl());
        }
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, ProfileResponseDTO.class);
    }
}
