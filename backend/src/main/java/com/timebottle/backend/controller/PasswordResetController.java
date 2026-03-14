package com.timebottle.backend.controller;

import com.timebottle.backend.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        
        if (email == null || email.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "邮箱不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        boolean success = passwordResetService.sendResetCode(email);
        
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("success", true);
            response.put("message", "验证码已发送到您的邮箱");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "该邮箱未注册");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/verify-reset-code")
    public ResponseEntity<?> verifyResetCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        
        if (email == null || email.isEmpty() || code == null || code.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "邮箱和验证码不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        boolean valid = passwordResetService.verifyCode(email, code);
        
        Map<String, Object> response = new HashMap<>();
        if (valid) {
            response.put("success", true);
            response.put("message", "验证码正确");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "验证码错误或已过期");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        String newPassword = request.get("newPassword");
        
        if (email == null || email.isEmpty() || code == null || code.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "参数不完整");
            return ResponseEntity.badRequest().body(response);
        }

        if (newPassword.length() < 6) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "密码长度不能少于6位");
            return ResponseEntity.badRequest().body(response);
        }

        boolean success = passwordResetService.resetPassword(email, code, newPassword);
        
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("success", true);
            response.put("message", "密码重置成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "重置失败，验证码错误或已过期");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
