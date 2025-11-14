package com.muxiaojie.myblog.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.muxiaojie.myblog.entity.Article;
import com.muxiaojie.myblog.entity.User;
import com.muxiaojie.myblog.mapper.ArticleMapper;

@Service
public class ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    // 获取所有已发布的文章（首页用）
    public List<Article> findAll() {
        return articleMapper.findAll();
    }
    
    // 获取所有文章（管理员用）
    public List<Article> findAllWithAllStatus() {
        return articleMapper.findAllWithAllStatus();
    }
    
    // 根据ID获取文章，并增加浏览量
    public Article findById(Integer id) {
        Article article = articleMapper.findById(id);
        if (article != null && article.isPublished()) {
            // 增加浏览量
            articleMapper.incrementViewCount(id);
            article.setViewCount(article.getViewCount() + 1);
        }
        return article;
    }
    
    // 根据作者ID获取文章
    public List<Article> findByAuthorId(Integer authorId) {
        return articleMapper.findByAuthorId(authorId);
    }
    
    // 根据作者ID和状态获取文章
    public List<Article> findByAuthorIdAndStatus(Integer authorId, String status) {
        return articleMapper.findByAuthorIdAndStatus(authorId, status);
    }
    
    // 保存文章（新增或更新）
    public boolean save(Article article, User currentUser) {
        if (article.getId() == null) {
            // 新增文章
            article.setAuthorId(currentUser.getId());
            
            // 设置默认状态
            if (article.getStatus() == null) {
                article.setStatus("PUBLISHED");
            }
            
            // 设置默认浏览量
            if (article.getViewCount() == null) {
                article.setViewCount(0);
            }
            
            // 自动生成摘要（如果未提供）
            if (article.getSummary() == null || article.getSummary().trim().isEmpty()) {
                article.setSummary(generateSummary(article.getContent()));
            }
            
            articleMapper.insert(article);
            return true;
        } else {
            // 更新文章 - 检查权限
            if (articleMapper.countByIdAndAuthor(article.getId(), currentUser.getId()) == 0) {
                return false; // 文章不存在或不是作者
            }
            
            // 自动生成摘要（如果未提供）
            if (article.getSummary() == null || article.getSummary().trim().isEmpty()) {
                article.setSummary(generateSummary(article.getContent()));
            }
            
            articleMapper.update(article);
            return true;
        }
    }
    
    // 删除文章
    public boolean delete(Integer id, User currentUser) {
        // 检查权限：作者或管理员可以删除
        if (articleMapper.countByIdAndAuthor(id, currentUser.getId()) == 0 && 
            !"ADMIN".equals(currentUser.getRole())) {
            return false; // 没有权限
        }
        
        articleMapper.delete(id);
        return true;
    }
    
    // 更新文章状态
    public boolean updateStatus(Integer id, String status, User currentUser) {
        // 检查权限
        if (articleMapper.countByIdAndAuthor(id, currentUser.getId()) == 0 && 
            !"ADMIN".equals(currentUser.getRole())) {
            return false;
        }
        
        articleMapper.updateStatus(id, status);
        return true;
    }
    
    // 发布文章
    public boolean publishArticle(Integer id, User currentUser) {
        return updateStatus(id, "PUBLISHED", currentUser);
    }
    
    // 将文章设为草稿
    public boolean draftArticle(Integer id, User currentUser) {
        return updateStatus(id, "DRAFT", currentUser);
    }
    
    // 检查用户是否是文章作者
    public boolean isAuthor(Integer articleId, Integer userId) {
        return articleMapper.countByIdAndAuthor(articleId, userId) > 0;
    }
    
    // 搜索文章（只在已发布文章中搜索）
    public List<Article> searchArticles(String keyword) {
        List<Article> allArticles = articleMapper.findAll();
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return allArticles;
        }
        
        String lowerKeyword = keyword.toLowerCase().trim();
        return allArticles.stream()
                .filter(article -> 
                    (article.getTitle() != null && article.getTitle().toLowerCase().contains(lowerKeyword)) ||
                    (article.getContent() != null && article.getContent().toLowerCase().contains(lowerKeyword)) ||
                    (article.getSummary() != null && article.getSummary().toLowerCase().contains(lowerKeyword))
                )
                .collect(Collectors.toList());
    }
    
    // 获取热门文章
    public List<Article> getPopularArticles(int limit) {
        return articleMapper.findPopularArticles(limit);
    }
    
    // 获取用户文章数量
    public int getUserArticleCount(Integer userId) {
        List<Article> articles = articleMapper.findByAuthorId(userId);
        return articles != null ? articles.size() : 0;
    }
    
    // 获取用户已发布文章数量
    public int getUserPublishedArticleCount(Integer userId) {
        List<Article> articles = articleMapper.findByAuthorIdAndStatus(userId, "PUBLISHED");
        return articles != null ? articles.size() : 0;
    }
    
    // 获取文章总数
    public int getTotalArticleCount() {
        List<Article> articles = articleMapper.findAll();
        return articles != null ? articles.size() : 0;
    }
    
    // 获取所有文章总数（包括草稿）
    public int getTotalArticleCountWithAllStatus() {
        List<Article> articles = articleMapper.findAllWithAllStatus();
        return articles != null ? articles.size() : 0;
    }
    
    // 自动生成摘要
    private String generateSummary(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        
        // 移除Markdown标记
        String text = content.replaceAll("#", "")
                            .replaceAll("\\*", "")
                            .replaceAll("`", "")
                            .replaceAll("\\[.*?\\]\\(.*?\\)", "") // 移除链接
                            .replaceAll("<.*?>", "") // 移除HTML标签
                            .trim();
        
        // 提取前100个字符
        if (text.length() <= 100) {
            return text;
        } else {
            // 在空格处截断，避免截断单词
            String summary = text.substring(0, 100);
            int lastSpace = summary.lastIndexOf(' ');
            if (lastSpace > 80) { // 确保不会截得太短
                summary = summary.substring(0, lastSpace);
            }
            return summary + "...";
        }
    }
}