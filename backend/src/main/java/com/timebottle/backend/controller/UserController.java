package com.timebottle.backend.controller;

import com.timebottle.backend.entity.User;
import com.timebottle.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Value("${app.upload.avatar-path:uploads/avatars}")
    private String avatarPath;

    @Value("${app.upload.max-size:2097152}")
    private long maxFileSize;

    // 允许的图片类型
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    // 修改用户信息
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestParam("username") String username,
            @RequestParam("nickname") String nickname,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) {
        try {
            // 从请求中获取用户名
            User user = userService.findByUsername(username);
            
            if (user == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "用户不存在");
                return ResponseEntity.badRequest().body(response);
            }

            // 更新昵称
            user.setNickname(nickname);

            // 更新密码（如果提供）
            if (password != null && !password.isEmpty()) {
                user.setPasswordHash(userService.encodePassword(password));
            }

            // 处理头像上传
            if (avatar != null && !avatar.isEmpty()) {
                // 验证文件大小
                if (avatar.getSize() > maxFileSize) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "文件大小超过限制（最大2MB）");
                    return ResponseEntity.badRequest().body(response);
                }

                // 验证文件类型
                String contentType = avatar.getContentType();
                if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "不支持的文件类型，仅支持 jpg、png、gif、webp 格式");
                    return ResponseEntity.badRequest().body(response);
                }

                // 验证文件扩展名
                String originalFilename = avatar.getOriginalFilename();
                if (originalFilename == null) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "文件名无效");
                    return ResponseEntity.badRequest().body(response);
                }
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase();
                if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "不支持的文件扩展名，仅支持 jpg、png、gif、webp 格式");
                    return ResponseEntity.badRequest().body(response);
                }

                // 确保头像目录存在
                File dir = new File(avatarPath);
                if (!dir.exists()) {
                    boolean created = dir.mkdirs();
                    if (!created) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("message", "无法创建上传目录");
                        return ResponseEntity.badRequest().body(response);
                    }
                }

                // 生成唯一文件名
                String fileName = UUID.randomUUID().toString() + fileExtension;

                // 保存文件 - 使用绝对路径
                File dest = new File(dir.getAbsoluteFile(), fileName);
                try {
                    avatar.transferTo(dest);
                } catch (IOException e) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "文件保存失败: " + e.getMessage());
                    return ResponseEntity.badRequest().body(response);
                }

                // 更新用户头像路径（只保存文件名）
                user.setAvatar(fileName);
            }

            // 保存更新后的用户信息
            userService.updateUser(user);

            // 构建响应
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("username", user.getUsername());
            userInfo.put("avatar", user.getAvatar());
            userInfo.put("nickname", user.getNickname());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "修改成功");
            response.put("user", userInfo);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
