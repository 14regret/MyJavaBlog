package com.muxiaojie.myblog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.muxiaojie.myblog.entity.User;
import com.muxiaojie.myblog.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // 显示登录页面 - 支持记住我自动登录
    @GetMapping("/user/login")
    public String showLoginForm(HttpServletRequest request, HttpSession session, Model model) {
        // 检查是否已经登录
        if (session.getAttribute("currentUser") != null) {
            return "redirect:/";
        }
        
        // 检查记住我cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("rememberMe".equals(cookie.getName())) {
                    User user = userService.validateRememberMeToken(cookie.getValue());
                    if (user != null) {
                        session.setAttribute("currentUser", user);
                        return "redirect:/";
                    }
                }
            }
        }
        
        return "user-login";
    }

    // 处理登录
    @PostMapping("/user/login")
    public String login(@RequestParam String username, 
                       @RequestParam String password,
                       @RequestParam(required = false) Boolean rememberMe,
                       HttpSession session, 
                       HttpServletResponse response,
                       Model model) {
        
        User user = userService.login(username, password);
        if (user != null) {
            // 登录成功，保存用户信息到session
            session.setAttribute("currentUser", user);
            
            // 记住我功能
            if (rememberMe != null && rememberMe) {
                String rememberToken = userService.generateRememberMeToken(user);
                Cookie rememberCookie = new Cookie("rememberMe", rememberToken);
                rememberCookie.setMaxAge(30 * 24 * 60 * 60); // 30天
                rememberCookie.setPath("/");
                rememberCookie.setHttpOnly(true);
                response.addCookie(rememberCookie);
            }
            
            return "redirect:/";
        } else {
            // 登录失败
            model.addAttribute("error", "用户名或密码错误");
            return "user-login";
        }
    }

    // 显示注册页面
    @GetMapping("/user/register")
    public String showRegisterForm(HttpSession session) {
        // 如果已经登录，重定向到首页
        if (session.getAttribute("currentUser") != null) {
            return "redirect:/";
        }
        return "user-register";
    }

    // 处理注册
    @PostMapping("/user/register")
    public String register(User user,
                          @RequestParam String confirmPassword,
                          @RequestParam(required = false) Boolean agreeTerms,
                          Model model) {
        
        // 基本验证
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            model.addAttribute("error", "用户名不能为空");
            return "user-register";
        }
        
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            model.addAttribute("error", "密码不能为空");
            return "user-register";
        }
        
        // 用户名长度验证
        if (user.getUsername().length() < 3 || user.getUsername().length() > 20) {
            model.addAttribute("error", "用户名长度必须在3-20个字符之间");
            return "user-register";
        }
        
        // 密码确认验证
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "两次输入的密码不一致");
            return "user-register";
        }
        
        // 条款同意验证
        if (agreeTerms == null || !agreeTerms) {
            model.addAttribute("error", "请同意服务条款和隐私政策");
            return "user-register";
        }
        
        // 密码强度验证
        if (!userService.isPasswordStrong(user.getPassword())) {
            model.addAttribute("error", "密码强度不足，请包含字母和数字，至少6位");
            return "user-register";
        }
        
        // 邮箱格式验证（如果提供了邮箱）
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            if (!isValidEmail(user.getEmail())) {
                model.addAttribute("error", "邮箱格式不正确");
                return "user-register";
            }
        }
        
        // 注册用户
        boolean success = userService.register(user);
        if (success) {
            model.addAttribute("message", "注册成功，请登录");
            return "user-login";
        } else {
            model.addAttribute("error", "用户名或邮箱已被注册");
            return "user-register";
        }
    }

    // 退出登录
    @GetMapping("/user/logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        // 获取当前用户并清除记住我token
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            userService.clearRememberToken(currentUser.getId());
        }
        
        // 清除session
        session.removeAttribute("currentUser");
        session.invalidate();
        
        // 清除记住我cookie
        Cookie rememberCookie = new Cookie("rememberMe", null);
        rememberCookie.setMaxAge(0);
        rememberCookie.setPath("/");
        response.addCookie(rememberCookie);
        
        return "redirect:/";
    }

    // 用户个人资料页面
    @GetMapping("/user/profile")
    public String showProfile(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        // 重新从数据库获取最新用户信息
        User user = userService.findById(currentUser.getId());
        model.addAttribute("user", user);
        return "user-profile";
    }

    // 更新用户资料
    @PostMapping("/user/profile")
    public String updateProfile(User user, 
                               HttpSession session, 
                               Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        // 设置ID为当前用户ID
        user.setId(currentUser.getId());
        
        // 邮箱格式验证（如果提供了邮箱）
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            if (!isValidEmail(user.getEmail())) {
                model.addAttribute("error", "邮箱格式不正确");
                model.addAttribute("user", user);
                return "user-profile";
            }
        }
        
        boolean success = userService.updateUser(user);
        if (success) {
            // 更新session中的用户信息
            User updatedUser = userService.findById(currentUser.getId());
            session.setAttribute("currentUser", updatedUser);
            
            model.addAttribute("message", "资料更新成功");
            model.addAttribute("user", updatedUser);
        } else {
            model.addAttribute("error", "资料更新失败");
            model.addAttribute("user", user);
        }
        
        return "user-profile";
    }

    // 邮箱格式验证
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}