package com.passport.creditpassport.userprofile.repository;

import com.passport.creditpassport.userprofile.models.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<UserProfile, String> {

    boolean existsByUserId(String userId);

    Optional<UserProfile> findByUserId(String userId);
}
