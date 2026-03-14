package com.timebottle.backend.repository;

import com.timebottle.backend.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByEmailAndTokenAndUsedFalse(String email, String token);
    
    Optional<PasswordResetToken> findTopByEmailAndUsedFalseOrderByCreatedAtDesc(String email);
    
    void deleteByEmail(String email);
    
    void deleteByExpiresAtBefore(LocalDateTime now);
}
