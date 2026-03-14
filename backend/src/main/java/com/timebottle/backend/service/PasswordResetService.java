package com.timebottle.backend.service;

import com.timebottle.backend.entity.PasswordResetToken;
import com.timebottle.backend.entity.User;
import com.timebottle.backend.repository.PasswordResetTokenRepository;
import com.timebottle.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private static final int CODE_LENGTH = 6;
    private static final int EXPIRATION_MINUTES = 5;

    @Transactional
    public boolean sendResetCode(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return false;
        }

        tokenRepository.deleteAllByEmail(email);

        String code = generateCode();
        
        PasswordResetToken token = new PasswordResetToken();
        token.setEmail(email);
        token.setToken(code);
        token.setExpiresAt(LocalDateTime.now(ZoneId.of("Asia/Shanghai")).plusMinutes(EXPIRATION_MINUTES));
        token.setUsed(false);
        tokenRepository.save(token);

        emailService.sendVerificationCode(email, code);
        return true;
    }

    public boolean verifyCode(String email, String code) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByEmailAndTokenAndUsedFalse(email, code);
        
        if (tokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken token = tokenOpt.get();
        
        if (token.getExpiresAt().isBefore(LocalDateTime.now(ZoneId.of("Asia/Shanghai")))) {
            return false;
        }

        return true;
    }

    @Transactional
    public boolean resetPassword(String email, String code, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByEmailAndTokenAndUsedFalse(email, code);
        
        if (tokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken token = tokenOpt.get();
        
        if (token.getExpiresAt().isBefore(LocalDateTime.now(ZoneId.of("Asia/Shanghai")))) {
            return false;
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return false;
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        token.setUsed(true);
        tokenRepository.save(token);

        return true;
    }

    public void cleanupExpiredTokens() {
        tokenRepository.deleteByExpiresAtBefore(LocalDateTime.now(ZoneId.of("Asia/Shanghai")));
    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
