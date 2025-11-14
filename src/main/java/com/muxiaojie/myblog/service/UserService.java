package com.muxiaojie.myblog.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.muxiaojie.myblog.entity.User;
import com.muxiaojie.myblog.mapper.UserMapper;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    // 用户注册
    public boolean register(User user) {
        // 检查用户名是否已存在
        if (userMapper.countByUsername(user.getUsername()) > 0) {
            return false;
        }
        
        // 检查邮箱是否已存在（如果提供了邮箱）
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty() 
            && userMapper.countByEmail(user.getEmail()) > 0) {
            return false;
        }
        
        // 设置默认角色和显示名
        user.setRole("USER");
        if (user.getDisplayName() == null || user.getDisplayName().trim().isEmpty()) {
            user.setDisplayName(user.getUsername());
        }
        
        // 设置创建时间
        user.setCreatedTime(LocalDateTime.now());
        
        // 密码加密
        String encryptedPassword = DigestUtils.md5DigestAsHex(
            user.getPassword().getBytes(StandardCharsets.UTF_8));
        user.setPassword(encryptedPassword);
        
        // 保存用户
        userMapper.insert(user);
        return true;
    }

    // 用户登录
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return null;
        }
        
        // 验证密码
        String encryptedPassword = DigestUtils.md5DigestAsHex(
            password.getBytes(StandardCharsets.UTF_8));
        
        if (user.getPassword().equals(encryptedPassword)) {
            // 更新最后登录时间
            user.setLastLoginTime(LocalDateTime.now());
            userMapper.updateLastLoginTime(user.getId());
            return user;
        }
        
        return null;
    }

    // 生成记住我token
    public String generateRememberMeToken(User user) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryTime = LocalDateTime.now().plusDays(30);
        
        // 更新用户的记住我token和过期时间
        userMapper.updateRememberToken(user.getId(), token, expiryTime);
        return token;
    }

    // 通过记住我token验证用户
    public User validateRememberMeToken(String token) {
        User user = userMapper.findByRememberToken(token);
        if (user != null && user.getTokenExpiryTime() != null 
            && user.getTokenExpiryTime().isAfter(LocalDateTime.now())) {
            return user;
        }
        return null;
    }

    // 检查用户名是否存在
    public boolean existsByUsername(String username) {
        return userMapper.countByUsername(username) > 0;
    }

    // 检查邮箱是否存在
    public boolean existsByEmail(String email) {
        return userMapper.countByEmail(email) > 0;
    }

    // 密码强度验证
    public boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        
        // 检查是否包含字母和数字
        boolean hasLetter = false;
        boolean hasDigit = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        
        return hasLetter && hasDigit;
    }

    // 根据ID获取用户
    public User findById(Integer id) {
        return userMapper.findById(id);
    }

    // 更新用户信息
    public boolean updateUser(User user) {
        // 检查邮箱是否被其他用户使用（如果修改了邮箱）
        User existingUser = userMapper.findById(user.getId());
        if (existingUser != null) {
            if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
                if (userMapper.countByEmail(user.getEmail()) > 0) {
                    return false; // 邮箱已被其他用户使用
                }
            }
            
            // 保留原有密码（如果不修改密码）
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                // 加密新密码
                String encryptedPassword = DigestUtils.md5DigestAsHex(
                    user.getPassword().getBytes(StandardCharsets.UTF_8));
                user.setPassword(encryptedPassword);
            }
            
            userMapper.update(user);
            return true;
        }
        return false;
    }

    // 清除记住我token
    public void clearRememberToken(Integer userId) {
        userMapper.clearRememberToken(userId);
    }

    // 获取用户数量（管理员功能）
    public int getUserCount() {
        // 这里需要添加一个统计用户数量的方法到UserMapper
        // 暂时返回0，可以在UserMapper中添加对应方法
        return 0;
    }
}