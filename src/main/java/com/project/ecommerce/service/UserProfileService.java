package com.project.ecommerce.service;

import com.project.ecommerce.dto.request.UserProfileRequest;
import com.project.ecommerce.dto.response.UserProfileResponse;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.entity.UserProfile;
import com.project.ecommerce.enums.FreeFrom;
import com.project.ecommerce.enums.SkinConcern;
import com.project.ecommerce.exception.NotFoundException;
import com.project.ecommerce.repository.UserProfileRepository;
import com.project.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile() {
        User currentUser = userService.getLoggedInUser();

        UserProfile profile = userProfileRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new NotFoundException("User profile not found"));

        return toResponse(profile);
    }

    @Transactional
    public UserProfileResponse createMyProfile(UserProfileRequest request) {
        User currentUser = userService.getLoggedInUser();

        if (userProfileRepository.existsByUserId(currentUser.getId())) {
            throw new IllegalStateException("User profile already exists");
        }

        UserProfile profile = UserProfile.builder()
                .user(currentUser)
                .skinType(request.getSkinType())
                .skinConcerns(toSafeSkinConcernSet(request.getSkinConcerns()))
                .avoidIngredients(toSafeFreeFromSet(request.getAvoidIngredients()))
                .age(request.getAge())
                .build();

        UserProfile savedProfile = userProfileRepository.save(profile);
        return toResponse(savedProfile);
    }

    @Transactional
    public UserProfileResponse updateMyProfile(UserProfileRequest request) {
        User currentUser = userService.getLoggedInUser();

        UserProfile profile = userProfileRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new NotFoundException("User profile not found"));

        if (request.getSkinType() != null) {
            profile.setSkinType(request.getSkinType());
        }

        if (request.getSkinConcerns() != null) {
            profile.setSkinConcerns(toSafeSkinConcernSet(request.getSkinConcerns()));
        }

        if (request.getAvoidIngredients() != null) {
            profile.setAvoidIngredients(toSafeFreeFromSet(request.getAvoidIngredients()));
        }

        if (request.getAge() != null) {
            profile.setAge(request.getAge());
        }

        UserProfile updatedProfile = userProfileRepository.save(profile);
        return toResponse(updatedProfile);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfileByUserId(Long userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("User profile not found for user id: " + userId));

        return toResponse(profile);
    }

    @Transactional
    public void createDefaultProfileIfAbsent(User user) {
        if (userProfileRepository.existsByUserId(user.getId())) {
            return;
        }

        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        UserProfile defaultProfile = UserProfile.builder()
                .user(managedUser)
                .skinConcerns(new HashSet<>())
                .avoidIngredients(new HashSet<>())
                .build();

        userProfileRepository.save(defaultProfile);
    }

    private UserProfileResponse toResponse(UserProfile profile) {
        return UserProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .userName(profile.getUser().getName())
                .email(profile.getUser().getEmail())
                .skinType(profile.getSkinType())
                .skinConcerns(toSafeSkinConcernSet(profile.getSkinConcerns()))
                .avoidIngredients(toSafeFreeFromSet(profile.getAvoidIngredients()))
                .age(profile.getAge())
                .build();
    }

    private Set<SkinConcern> toSafeSkinConcernSet(Set<SkinConcern> source) {
        return source == null ? new HashSet<>() : new HashSet<>(source);
    }

    private Set<FreeFrom> toSafeFreeFromSet(Set<FreeFrom> source) {
        return source == null ? new HashSet<>() : new HashSet<>(source);
    }
}
