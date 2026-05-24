package com.passport.creditpassport.userprofile.service;

import com.passport.creditpassport.userprofile.dto.ProfileResponse;
import com.passport.creditpassport.userprofile.dto.UpdateProfileRequest;

public interface ProfileService {
    ProfileResponse createProfile(String userId, String name, String email, String number, String natId, UpdateProfileRequest request);
    ProfileResponse getProfile(String userId, String name, String email, String number, String natId);
    ProfileResponse updateProfile(String userId, String name, String email, String number, String natId, UpdateProfileRequest request);
}

