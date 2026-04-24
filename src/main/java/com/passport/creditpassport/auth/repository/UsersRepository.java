package com.passport.creditpassport.auth.repository;

import com.passport.creditpassport.auth.models.user;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<user, String> {

    Optional<User> findByUserName(String userName);
}
