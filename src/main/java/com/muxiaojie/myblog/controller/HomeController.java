package com.muxiaojie.myblog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.muxiaojie.myblog.entity.Article;
import com.muxiaojie.myblog.entity.User; // 添加这行导入
import com.muxiaojie.myblog.mapper.ArticleMapper;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    private final ArticleMapper articleMapper;

    @Autowired
    public HomeController(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    // 博客首页 - 显示文章列表
    @GetMapping("/")
    public String index(Model model) {
        List<Article> articles = articleMapper.findAll();
        model.addAttribute("articles", articles);
        return "index";
    }

    // 文章详情页
    @GetMapping("/article/{id}")
    public String articleDetail(@PathVariable("id") Integer id, Model model) {
        Article article = articleMapper.findById(id);
        if (article == null) {
            return "redirect:/";
        }
        model.addAttribute("article", article);
        return "article-detail";
    }

    // 显示文章编辑页面（用于发布新文章）
    @GetMapping("/article/edit")
    public String showCreateForm(Model model) {
        model.addAttribute("article", new Article());
        return "article-edit";
    }

    // 显示文章编辑页面（用于编辑已有文章）
    @GetMapping("/article/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        Article article = articleMapper.findById(id);
        if (article == null) {
            return "redirect:/";
        }
        model.addAttribute("article", article);
        return "article-edit";
    }

    // 处理文章保存（发布新文章或更新文章）
    @PostMapping("/article/save")
    public String saveArticle(Article article, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        if (article.getId() == null) {
            // 新增文章，设置当前用户为作者
            article.setAuthorId(currentUser.getId());
            articleMapper.insert(article);
        } else {
            // 更新文章，检查权限（简化版，后面可以增强）
            Article existingArticle = articleMapper.findById(article.getId());
            if (existingArticle != null && existingArticle.getAuthorId().equals(currentUser.getId())) {
                articleMapper.update(article);
            }
        }
        return "redirect:/article/" + article.getId();
    }

    // 删除文章（添加权限检查）
    @GetMapping("/article/delete/{id}")
    public String deleteArticle(@PathVariable("id") Integer id, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        Article article = articleMapper.findById(id);
        if (article != null && article.getAuthorId().equals(currentUser.getId())) {
            articleMapper.delete(id);
        }
        
        return "redirect:/";
    }

    // 文章管理页面
    @GetMapping("/admin/articles")
    public String articleManagement(Model model) {
        List<Article> articles = articleMapper.findAll();
        model.addAttribute("articles", articles);
        return "article-management";
    }
}