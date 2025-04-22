package com.cloudify.demologin.repository;

import com.cloudify.demologin.entity.UserOTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserOTPRepository extends JpaRepository<UserOTP, UUID> {
    @Query("select u from UserOTP u where u.email = ?1")
    Optional<UserOTP> findByEmail(String username);
    
    Optional<UserOTP> findByEmailAndOtpAndIsVerifiedFalseAndExpirationTimeAfter(
            String email, String otp, LocalDateTime now);
}

