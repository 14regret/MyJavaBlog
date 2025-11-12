package com.muxiaojie.myblog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/test")
    public String test() {
        try {
            // 测试数据库连接
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return "数据库连接成功！测试结果：" + result;
        } catch (Exception e) {
            return "数据库连接失败：" + e.getMessage();
        }
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring Boot 运行成功！";
    }
}