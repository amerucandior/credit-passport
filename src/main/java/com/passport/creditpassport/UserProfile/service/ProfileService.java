package com.passport.creditpassport.UserProfile.service;

import com.passport.creditpassport.UserProfile.dto.ProfileResponse;
import com.passport.creditpassport.UserProfile.dto.UpdateProfileRequest;
import com.passport.creditpassport.auth.models.User;

public interface ProfileService {
    ProfileResponse createProfile(User authenticatedUser, UpdateProfileRequest request);
    ProfileResponse getProfile(User authenticatedUser);
    ProfileResponse updateProfile(User authenticatedUser, UpdateProfileRequest request);
}

