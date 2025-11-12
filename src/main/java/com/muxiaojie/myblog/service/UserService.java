package com.muxiaojie.myblog.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

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
            userMapper.updateLastLoginTime(user.getId());
            return user;
        }
        
        return null;
    }
}