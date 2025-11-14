package com.muxiaojie.myblog.entity;

import java.time.LocalDateTime;

public class Article {
    private Integer id;
    private String title;
    private String content;
    private String summary;
    private Integer authorId;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private String authorName;
    
    // 新增字段
    private String status = "PUBLISHED"; // 文章状态：PUBLISHED, DRAFT
    private Integer viewCount = 0;       // 阅读量
    
    // 构造方法
    public Article() {}
    
    public Article(Integer id, String title, String content, String summary, Integer authorId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.summary = summary;
        this.authorId = authorId;
    }
    
    // Getter 和 Setter 方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
    
    // 实用方法
    public boolean isPublished() {
        return "PUBLISHED".equals(this.status);
    }
    
    public boolean isDraft() {
        return "DRAFT".equals(this.status);
    }
}