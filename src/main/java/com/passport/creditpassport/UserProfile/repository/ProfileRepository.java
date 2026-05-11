package com.passport.creditpassport.UserProfile.repository;

import com.passport.creditpassport.UserProfile.models.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<UserProfile, String> {

    boolean existsByUser_Id(String userId);
    Optional<UserProfile> findByUser_Id(String userId);
}
