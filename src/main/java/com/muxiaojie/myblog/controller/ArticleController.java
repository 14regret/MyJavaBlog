package com.muxiaojie.myblog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.muxiaojie.myblog.entity.Article;
import com.muxiaojie.myblog.entity.User;
import com.muxiaojie.myblog.service.ArticleService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    // 文章详情页
    @GetMapping("/article/{id}")
    public String articleDetail(@PathVariable Integer id, Model model, HttpSession session) {
        Article article = articleService.findById(id);
        if (article == null) {
            return "redirect:/";
        }
        
        // 检查权限：只有作者或管理员可以查看草稿
        User currentUser = (User) session.getAttribute("currentUser");
        if (article.isDraft()) {
            if (currentUser == null || 
                (!articleService.isAuthor(id, currentUser.getId()) && !"ADMIN".equals(currentUser.getRole()))) {
                return "redirect:/";
            }
        }
        
        model.addAttribute("article", article);
        model.addAttribute("isAuthor", currentUser != null && articleService.isAuthor(id, currentUser.getId()));
        return "article-detail";
    }

    // 显示文章编辑页面（新增或编辑）
    @GetMapping("/article/edit")
    public String showEditForm(@RequestParam(required = false) Integer id, 
                              Model model, 
                              HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        Article article;
        if (id != null) {
            // 编辑现有文章
            article = articleService.findById(id);
            if (article == null || !articleService.isAuthor(id, currentUser.getId())) {
                return "redirect:/";
            }
            model.addAttribute("isEdit", true);
        } else {
            // 新增文章
            article = new Article();
            model.addAttribute("isEdit", false);
        }
        
        model.addAttribute("article", article);
        return "article-edit";
    }

    // 保存文章（新增或更新）
    @PostMapping("/article/save")
    public String saveArticle(Article article, 
                            @RequestParam(required = false) String saveAsDraft,
                            HttpSession session, 
                            Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        // 基本验证
        if (article.getTitle() == null || article.getTitle().trim().isEmpty()) {
            model.addAttribute("error", "文章标题不能为空");
            model.addAttribute("article", article);
            model.addAttribute("isEdit", article.getId() != null);
            return "article-edit";
        }
        
        if (article.getContent() == null || article.getContent().trim().isEmpty()) {
            model.addAttribute("error", "文章内容不能为空");
            model.addAttribute("article", article);
            model.addAttribute("isEdit", article.getId() != null);
            return "article-edit";
        }
        
        // 设置文章状态
        if ("draft".equals(saveAsDraft)) {
            article.setStatus("DRAFT");
        } else {
            article.setStatus("PUBLISHED");
        }
        
        boolean success = articleService.save(article, currentUser);
        if (success) {
            if (article.isDraft()) {
                return "redirect:/my/drafts";
            } else {
                return "redirect:/article/" + article.getId();
            }
        } else {
            model.addAttribute("error", "保存失败，请检查权限或重试");
            model.addAttribute("article", article);
            model.addAttribute("isEdit", article.getId() != null);
            return "article-edit";
        }
    }

    // 删除文章
    @GetMapping("/article/delete/{id}")
    public String deleteArticle(@PathVariable Integer id, 
                               HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        articleService.delete(id, currentUser);
        return "redirect:/admin/articles";
    }

    // 发布文章
    @GetMapping("/article/publish/{id}")
    public String publishArticle(@PathVariable Integer id, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        articleService.publishArticle(id, currentUser);
        return "redirect:/article/" + id;
    }

    // 设为草稿
    @GetMapping("/article/draft/{id}")
    public String draftArticle(@PathVariable Integer id, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        articleService.draftArticle(id, currentUser);
        return "redirect:/my/drafts";
    }

    // 文章管理页面（管理员）
    @GetMapping("/admin/articles")
    public String articleManagement(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        List<Article> articles;
        if ("ADMIN".equals(currentUser.getRole())) {
            // 管理员可以查看所有文章（包括草稿）
            articles = articleService.findAllWithAllStatus();
        } else {
            // 普通用户只能查看自己的文章
            articles = articleService.findByAuthorId(currentUser.getId());
        }
        
        model.addAttribute("articles", articles);
        return "article-management";
    }
    
    // 我的文章页面（已发布）
    @GetMapping("/my/articles")
    public String myArticles(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        List<Article> articles = articleService.findByAuthorIdAndStatus(currentUser.getId(), "PUBLISHED");
        model.addAttribute("articles", articles);
        model.addAttribute("articleCount", articles.size());
        return "my-articles";
    }
    
    // 我的草稿页面
    @GetMapping("/my/drafts")
    public String myDrafts(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        List<Article> drafts = articleService.findByAuthorIdAndStatus(currentUser.getId(), "DRAFT");
        model.addAttribute("drafts", drafts);
        model.addAttribute("draftCount", drafts.size());
        return "my-drafts";
    }
    
    // 搜索文章
    @GetMapping("/search")
    public String searchArticles(@RequestParam(required = false) String keyword, Model model) {
        List<Article> articles = articleService.searchArticles(keyword);
        model.addAttribute("articles", articles);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        return "search-results";
    }
    
    // 热门文章
    @GetMapping("/popular")
    public String popularArticles(Model model) {
        List<Article> popularArticles = articleService.getPopularArticles(10);
        model.addAttribute("articles", popularArticles);
        return "popular-articles";
    }
}