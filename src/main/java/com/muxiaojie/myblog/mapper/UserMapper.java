package com.muxiaojie.myblog.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
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
    @Insert("INSERT INTO users(username, password, email, display_name, role) " +
            "VALUES(#{username}, #{password}, #{email}, #{displayName}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);
    
    // 更新用户最后登录时间
    @Update("UPDATE users SET last_login_time = CURRENT_TIMESTAMP WHERE id = #{id}")
    void updateLastLoginTime(Integer id);
    
    // 检查用户名是否存在
    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    int countByUsername(String username);
}