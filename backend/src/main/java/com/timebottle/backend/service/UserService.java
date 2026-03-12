package com.timebottle.backend.service;

import com.timebottle.backend.entity.User;
import com.timebottle.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.TimeZone;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private Date getBeijingTime() {
        return new Date(System.currentTimeMillis() + TimeZone.getTimeZone("Asia/Shanghai").getRawOffset() 
            - TimeZone.getDefault().getRawOffset());
    }

    // 注册新用户
    public User register(String username, String password, String email) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("邮箱已存在");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRole("0");
        user.setPoints(0);
        user.setStatus("1");
        user.setAvatar("default-avatar.svg"); // 默认头像（只保存文件名）
        user.setNickname("未命名"); // 默认昵称
        user.setCreatedAt(getBeijingTime());
        user.setUpdatedAt(getBeijingTime());

        return userRepository.save(user);
    }

    // 根据用户名查找用户
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // 根据邮箱查找用户
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // 验证用户密码
    public boolean verifyPassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPasswordHash());
    }

    // 更新用户最后登录时间
    public void updateLastLoginTime(User user) {
        user.setLastLoginAt(getBeijingTime());
        userRepository.save(user);
    }

    // 更新用户信息
    public void updateUser(User user) {
        user.setUpdatedAt(getBeijingTime());
        userRepository.save(user);
    }

    // 加密密码
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}