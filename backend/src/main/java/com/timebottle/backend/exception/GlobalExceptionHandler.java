package com.timebottle.backend.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Map<String, Object>> handleExpiredJwtException(ExpiredJwtException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "登录已过期，请重新登录");
        response.put("error", "TOKEN_EXPIRED");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, Object>> handleJwtException(JwtException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "无效的认证信息");
        response.put("error", "INVALID_TOKEN");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
}
