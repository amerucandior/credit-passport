package com.passport.creditpassport.auth.repository;

import com.passport.creditpassport.auth.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, String> {
//    Existence checks used by duplicate guards and authServiceImpl
    boolean existsByName(String name);
    boolean existsByNatId(String natId);
    boolean existsByNumber(String number);
    boolean existsByEmail(String email);

    Optional<User> findByEmailOrNatId(String email, String natId);
    Optional<User> findByEmail(String email);

    // Used by LoginRequest when national ID is the login identifier
    Optional<User> findByNatId(String natId);}
