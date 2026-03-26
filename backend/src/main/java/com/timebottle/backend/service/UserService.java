package com.timebottle.backend.service;

import com.timebottle.backend.entity.User;
import com.timebottle.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Page<User> getUsers(String keyword, String role, String status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<User> userPage;
        
        if (keyword != null && !keyword.isEmpty()) {
            userPage = userRepository.search(keyword, pageRequest);
        } else {
            userPage = userRepository.findAll(pageRequest);
        }
        
        List<User> filteredUsers = userPage.getContent();
        
        if (role != null && !role.isEmpty()) {
            filteredUsers = filteredUsers.stream()
                .filter(u -> u.getRole().equals(role))
                .collect(Collectors.toList());
        }
        
        if (status != null && !status.isEmpty()) {
            filteredUsers = filteredUsers.stream()
                .filter(u -> u.getStatus().equals(status))
                .collect(Collectors.toList());
        }
        
        return new PageImpl<>(filteredUsers, pageRequest, userPage.getTotalElements());
    }

    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User updateUser(Integer id, String nickname, String role, String status, Integer points) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        User user = userOpt.get();
        if (nickname != null) user.setNickname(nickname);
        if (role != null) user.setRole(role);
        if (status != null) user.setStatus(status);
        if (points != null) user.setPoints(points);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("用户不存在");
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public void deleteUsers(List<Integer> ids) {
        userRepository.deleteAllById(ids);
    }

    public long countUsers() {
        return userRepository.count();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Transactional
    public User updateUser(User user) {
        user.setUpdatedAt(new Date());
        return userRepository.save(user);
    }

    @Transactional
    public User register(String username, String password, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setNickname("未命名");
        user.setRole("0");
        user.setPoints(0);
        user.setStatus("1");
        user.setAvatar("default-avatar.svg");
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        
        return userRepository.save(user);
    }

    public boolean verifyPassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPasswordHash());
    }

    @Transactional
    public void updateLastLoginTime(User user) {
        user.setLastLoginAt(new Date());
        userRepository.save(user);
    }
}
