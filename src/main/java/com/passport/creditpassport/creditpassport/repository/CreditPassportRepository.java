package com.passport.creditpassport.creditpassport.repository;

import com.passport.creditpassport.creditpassport.model.CreditPassport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CreditPassportRepository extends JpaRepository<CreditPassport, UUID> {
    Optional<CreditPassport> findByNationalId(String nationalId);
}
