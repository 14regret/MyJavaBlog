package com.muxiaojie.myblog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.muxiaojie.myblog.entity.User;
import com.muxiaojie.myblog.service.UserService;

import jakarta.servlet.http.HttpSession; // 改为 jakarta.servlet

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // 显示登录页面
    @GetMapping("/user/login")
    public String showLoginForm() {
        return "user-login";
    }

    // 处理登录
    @PostMapping("/user/login")
    public String login(@RequestParam String username, 
                       @RequestParam String password,
                       HttpSession session, Model model) {
        
        User user = userService.login(username, password);
        if (user != null) {
            // 登录成功，保存用户信息到session
            session.setAttribute("currentUser", user);
            return "redirect:/";
        } else {
            // 登录失败
            model.addAttribute("error", "用户名或密码错误");
            return "user-login";
        }
    }

    // 显示注册页面
    @GetMapping("/user/register")
    public String showRegisterForm() {
        return "user-register";
    }

    // 处理注册
    @PostMapping("/user/register")
    public String register(User user, Model model) {
        boolean success = userService.register(user);
        if (success) {
            model.addAttribute("message", "注册成功，请登录");
            return "user-login";
        } else {
            model.addAttribute("error", "用户名已存在");
            return "user-register";
        }
    }

    // 退出登录
    @GetMapping("/user/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("currentUser");
        return "redirect:/";
    }
}