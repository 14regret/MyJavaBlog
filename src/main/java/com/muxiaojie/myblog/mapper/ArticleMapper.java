package com.muxiaojie.myblog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.muxiaojie.myblog.entity.Article;

@Mapper
public interface ArticleMapper {
    
    // 查询所有文章
    @Select("SELECT * FROM articles ORDER BY created_time DESC")
    List<Article> findAll();
    
    // 根据ID查询文章
    @Select("SELECT * FROM articles WHERE id = #{id}")
    Article findById(Integer id);
    
    // 插入文章
    @Insert("INSERT INTO articles(title, content, author_id) VALUES(#{title}, #{content}, #{authorId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Article article);
    
    // 更新文章
    @Update("UPDATE articles SET title=#{title}, content=#{content} WHERE id=#{id}")
    void update(Article article);
    
    // 删除文章
    @Delete("DELETE FROM articles WHERE id = #{id}")
    void delete(Integer id);
}