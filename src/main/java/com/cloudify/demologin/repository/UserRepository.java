package com.cloudify.demologin.repository;

import com.cloudify.demologin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("select u from User u where u.username = ?1 or u.email = ?1")
    Optional<User> findByUsername(String username);
    
    @Query("select u from User u where u.email = ?1")
    Optional<User> findByEmail(String email);
}
