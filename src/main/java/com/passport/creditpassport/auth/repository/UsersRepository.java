package com.passport.creditpassport.auth.repository;

import com.passport.creditpassport.auth.models.user;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<user, String> {

    boolean existsByName(String name);
    boolean existsByNatId(String natId);
    boolean existsByNumber(String number);
    boolean existsByEmail(String email);

    Optional<user> findByEmailOrNatId(String email, String natId);
    Optional<user> findByEmail(String email);
}
