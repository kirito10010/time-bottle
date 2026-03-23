package com.timebottle.backend.controller;

import com.timebottle.backend.entity.User;
import com.timebottle.backend.service.UserService;
import com.timebottle.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");
            String email = request.get("email");

            User user = userService.register(username, password, email);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "注册成功");
            response.put("user", user.getUsername());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        User user = userService.findByUsername(username);
        if (user == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "用户名或密码错误");
            return ResponseEntity.badRequest().body(response);
        }

        if (!userService.verifyPassword(user, password)) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "用户名或密码错误");
            return ResponseEntity.badRequest().body(response);
        }

        userService.updateLastLoginTime(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "登录成功");
        response.put("token", token);
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("role", user.getRole());
        userInfo.put("points", user.getPoints());
        response.put("user", userInfo);

        return ResponseEntity.ok(response);
    }
}
