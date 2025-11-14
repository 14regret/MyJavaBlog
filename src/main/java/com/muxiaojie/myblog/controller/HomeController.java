package com.muxiaojie.myblog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.muxiaojie.myblog.entity.Article;
import com.muxiaojie.myblog.entity.User;
import com.muxiaojie.myblog.service.ArticleService;
import com.muxiaojie.myblog.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    private ArticleService articleService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        List<Article> articles = articleService.findAll();
        model.addAttribute("articles", articles);
        
        // 添加统计信息
        model.addAttribute("totalArticles", articleService.getTotalArticleCount());
        model.addAttribute("totalUsers", userService.getUserCount());
        
        // 添加用户文章数量统计（如果已登录）
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            int articleCount = articleService.getUserArticleCount(currentUser.getId());
            model.addAttribute("userArticleCount", articleCount);
        }
        
        return "index";
    }
    
    // 关于页面
    @GetMapping("/about")
    public String about() {
        return "about";
    }
    
    // 联系页面
    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
}