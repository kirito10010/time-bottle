package com.timebottle.backend.controller;

import com.timebottle.backend.entity.User;
import com.timebottle.backend.service.UserService;
import com.timebottle.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    private boolean isAdmin(String token) {
        if (token == null || token.isEmpty()) return false;
        String cleanToken = token.replace("Bearer ", "");
        Integer userId = jwtUtil.extractUserId(cleanToken);
        if (userId == null) return false;
        User user = userService.getUserById(userId).orElse(null);
        return user != null && "2".equals(user.getRole());
    }

    private ResponseEntity<Map<String, Object>> forbiddenResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "权限不足，仅管理员可访问");
        return ResponseEntity.status(403).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getUsers(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        if (!isAdmin(token)) {
            return forbiddenResponse();
        }
        
        Page<User> userPage = userService.getUsers(keyword, role, status, page, size);
        
        List<Map<String, Object>> users = userPage.getContent().stream().map(user -> {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            userMap.put("email", user.getEmail());
            userMap.put("nickname", user.getNickname());
            userMap.put("role", user.getRole());
            userMap.put("points", user.getPoints());
            userMap.put("status", user.getStatus());
            userMap.put("avatar", user.getAvatar());
            userMap.put("lastLoginAt", user.getLastLoginAt());
            userMap.put("createdAt", user.getCreatedAt());
            userMap.put("updatedAt", user.getUpdatedAt());
            return userMap;
        }).toList();
        
        Map<String, Object> response = new HashMap<>();
        response.put("users", users);
        response.put("currentPage", userPage.getNumber());
        response.put("totalPages", userPage.getTotalPages());
        response.put("totalElements", userPage.getTotalElements());
        response.put("size", userPage.getSize());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Integer id) {
        
        if (!isAdmin(token)) {
            return forbiddenResponse();
        }
        
        return userService.getUserById(id)
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("username", user.getUsername());
                    userMap.put("email", user.getEmail());
                    userMap.put("nickname", user.getNickname());
                    userMap.put("role", user.getRole());
                    userMap.put("points", user.getPoints());
                    userMap.put("status", user.getStatus());
                    userMap.put("avatar", user.getAvatar());
                    userMap.put("lastLoginAt", user.getLastLoginAt());
                    userMap.put("createdAt", user.getCreatedAt());
                    userMap.put("updatedAt", user.getUpdatedAt());
                    return ResponseEntity.ok(userMap);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Integer id, 
            @RequestBody Map<String, Object> request) {
        
        if (!isAdmin(token)) {
            return forbiddenResponse();
        }
        
        try {
            String nickname = (String) request.get("nickname");
            String role = (String) request.get("role");
            String status = (String) request.get("status");
            Integer points = request.get("points") != null 
                ? ((Number) request.get("points")).intValue() 
                : null;

            User user = userService.updateUser(id, nickname, role, status, points);
            
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            userMap.put("email", user.getEmail());
            userMap.put("nickname", user.getNickname());
            userMap.put("role", user.getRole());
            userMap.put("points", user.getPoints());
            userMap.put("status", user.getStatus());
            userMap.put("avatar", user.getAvatar());
            userMap.put("lastLoginAt", user.getLastLoginAt());
            userMap.put("createdAt", user.getCreatedAt());
            userMap.put("updatedAt", user.getUpdatedAt());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "更新成功");
            response.put("user", userMap);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Integer id) {
        
        if (!isAdmin(token)) {
            return forbiddenResponse();
        }
        
        try {
            userService.deleteUser(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/batch")
    public ResponseEntity<?> deleteUsers(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, List<Integer>> request) {
        
        if (!isAdmin(token)) {
            return forbiddenResponse();
        }
        
        try {
            List<Integer> ids = request.get("ids");
            if (ids == null || ids.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "请选择要删除的用户");
                return ResponseEntity.badRequest().body(response);
            }
            userService.deleteUsers(ids);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "批量删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
