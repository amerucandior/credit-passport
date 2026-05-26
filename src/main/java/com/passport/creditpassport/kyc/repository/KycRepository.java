package com.passport.creditpassport.kyc.repository;

import com.passport.creditpassport.kyc.model.KYC;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KycRepository extends JpaRepository<KYC, String> {
    Boolean existsByUserId(String userId);
    Optional<KYC> findByUserId(String userId);
}
