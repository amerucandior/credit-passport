package com.passport.creditpassport.UserProfile.service;

import com.passport.creditpassport.UserProfile.dto.ProfileResponse;
import com.passport.creditpassport.UserProfile.dto.UpdateProfileRequest;

public interface ProfileService {
    ProfileResponse createProfile(String userId, UpdateProfileRequest request);
    ProfileResponse getProfile(String userId);
    ProfileResponse updateProfile(String userId, UpdateProfileRequest request);
}

