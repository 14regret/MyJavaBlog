package com.muxiaojie.myblog.entity;

import java.time.LocalDateTime;

public class User {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String displayName;
    private String role = "USER"; // 默认角色
    private LocalDateTime createdTime;
    private LocalDateTime lastLoginTime;
    
    // 新增字段用于记住我功能
    private String rememberToken;
    private LocalDateTime tokenExpiryTime;
    
    // 构造器
    public User() {
        this.createdTime = LocalDateTime.now();
        this.role = "USER"; // 默认角色
    }
    
    // 获取有效的显示名称
    public String getEffectiveDisplayName() {
        return (displayName != null && !displayName.trim().isEmpty()) ? displayName : username;
    }

    // 生成Getter和Setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getRememberToken() {
        return rememberToken;
    }

    public void setRememberToken(String rememberToken) {
        this.rememberToken = rememberToken;
    }

    public LocalDateTime getTokenExpiryTime() {
        return tokenExpiryTime;
    }

    public void setTokenExpiryTime(LocalDateTime tokenExpiryTime) {
        this.tokenExpiryTime = tokenExpiryTime;
    }
}