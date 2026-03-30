package com.timebottle.backend.controller;

import com.timebottle.backend.entity.PointsLog;
import com.timebottle.backend.service.PointsService;
import com.timebottle.backend.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/points")
public class PointsController {

    @Autowired
    private PointsService pointsService;

    @Autowired
    private JwtUtil jwtUtil;

    private Integer extractUserIdFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        try {
            return jwtUtil.extractUserId(token.replace("Bearer ", ""));
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("TOKEN_EXPIRED");
        } catch (JwtException e) {
            return null;
        }
    }

    @PostMapping("/sign-in")
    public ResponseEntity<Map<String, Object>> signIn(@RequestHeader("Authorization") String token) {
        Integer userId;
        try {
            userId = extractUserIdFromToken(token);
        } catch (RuntimeException e) {
            if ("TOKEN_EXPIRED".equals(e.getMessage())) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "登录已过期，请重新登录");
                error.put("error", "TOKEN_EXPIRED");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            throw e;
        }
        if (userId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "无效的认证信息");
            return ResponseEntity.badRequest().body(error);
        }
        Map<String, Object> result = pointsService.signIn(userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSignInStatus(@RequestHeader("Authorization") String token) {
        Integer userId;
        try {
            userId = extractUserIdFromToken(token);
        } catch (RuntimeException e) {
            if ("TOKEN_EXPIRED".equals(e.getMessage())) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "登录已过期，请重新登录");
                error.put("error", "TOKEN_EXPIRED");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            throw e;
        }
        if (userId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("hasSignedIn", false);
            error.put("points", 0);
            return ResponseEntity.ok(error);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("hasSignedIn", pointsService.hasSignedInToday(userId));
        result.put("points", pointsService.getUserPoints(userId));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/logs")
    public ResponseEntity<List<PointsLog>> getPointsLogs(@RequestHeader("Authorization") String token) {
        Integer userId;
        try {
            userId = extractUserIdFromToken(token);
        } catch (RuntimeException e) {
            if ("TOKEN_EXPIRED".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            throw e;
        }
        if (userId == null) {
            return ResponseEntity.ok(List.of());
        }
        List<PointsLog> logs = pointsService.getUserPointsLogs(userId);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> getPointsBalance(@RequestHeader("Authorization") String token) {
        Integer userId;
        try {
            userId = extractUserIdFromToken(token);
        } catch (RuntimeException e) {
            if ("TOKEN_EXPIRED".equals(e.getMessage())) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "登录已过期，请重新登录");
                error.put("error", "TOKEN_EXPIRED");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            throw e;
        }
        if (userId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("points", 0);
            return ResponseEntity.ok(error);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("points", pointsService.getUserPoints(userId));
        return ResponseEntity.ok(result);
    }
}
