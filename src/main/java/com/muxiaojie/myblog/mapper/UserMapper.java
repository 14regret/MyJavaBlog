package com.muxiaojie.myblog.mapper;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.muxiaojie.myblog.entity.User;

@Mapper
public interface UserMapper {
    
    // 根据用户名查询用户
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);
    
    // 根据ID查询用户
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Integer id);
    
    // 插入新用户
    @Insert("INSERT INTO users(username, password, email, display_name, role, created_time) " +
            "VALUES(#{username}, #{password}, #{email}, #{displayName}, #{role}, #{createdTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);
    
    // 更新用户最后登录时间
    @Update("UPDATE users SET last_login_time = CURRENT_TIMESTAMP WHERE id = #{id}")
    void updateLastLoginTime(Integer id);
    
    // 检查用户名是否存在
    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    int countByUsername(String username);
    
    // ========== 新增方法 ==========
    
    // 根据邮箱查询用户
    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(String email);
    
    // 根据记住我token查询用户
    @Select("SELECT * FROM users WHERE remember_token = #{rememberToken}")
    User findByRememberToken(String rememberToken);
    
    // 检查邮箱是否存在
    @Select("SELECT COUNT(*) FROM users WHERE email = #{email}")
    int countByEmail(String email);
    
    // 更新记住我token和过期时间
    @Update("UPDATE users SET remember_token = #{token}, token_expiry_time = #{expiryTime} WHERE id = #{id}")
    void updateRememberToken(@Param("id") Integer id, 
                            @Param("token") String token, 
                            @Param("expiryTime") LocalDateTime expiryTime);
    
    // 更新用户信息
    @Update("UPDATE users SET email = #{email}, display_name = #{displayName}, password = #{password} WHERE id = #{id}")
    int update(User user);
    
    // 清除记住我token
    @Update("UPDATE users SET remember_token = NULL, token_expiry_time = NULL WHERE id = #{id}")
    void clearRememberToken(Integer id);
    
    // 获取所有用户数量（管理员功能）
    @Select("SELECT COUNT(*) FROM users")
    int count();
}