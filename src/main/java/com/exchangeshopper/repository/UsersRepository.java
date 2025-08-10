package com.exchangeshopper.repository;

import com.exchangeshopper.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
    Optional<Users> findByConfirmationToken(String token);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<Users> findById(Long id);
}

