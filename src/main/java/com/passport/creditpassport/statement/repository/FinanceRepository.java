package com.passport.creditpassport.statement.repository;

import com.passport.creditpassport.statement.models.Statement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FinanceRepository extends JpaRepository<Statement, UUID> {

    List<Statement> findByUserId(String userId);
    Optional<Statement> findByIdAndUserId(UUID id, String userId);

}
