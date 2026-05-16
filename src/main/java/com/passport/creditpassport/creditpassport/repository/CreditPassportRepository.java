package com.passport.creditpassport.creditpassport.repository;

import com.passport.creditpassport.creditpassport.model.CreditPassport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CreditPassportRepository extends JpaRepository<CreditPassport, String> {
    Optional<CreditPassport> findByNationalId(String nationalId);
}
