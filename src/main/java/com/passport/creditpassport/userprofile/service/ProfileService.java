package com.passport.creditpassport.userprofile.service;

import com.passport.creditpassport.userprofile.dto.ProfileResponse;
import com.passport.creditpassport.userprofile.dto.UpdateProfileRequest;

public interface ProfileService {
    ProfileResponse createProfile(String userId, UpdateProfileRequest request);
    ProfileResponse getProfile(String userId);
    ProfileResponse updateProfile(String userId, UpdateProfileRequest request);
}

