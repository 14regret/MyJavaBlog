package com.muxiaojie.myblog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.muxiaojie.myblog.entity.Article;

@Mapper
public interface ArticleMapper {
    
    // 查询所有已发布的文章（包含作者信息）
    @Select("SELECT a.*, u.display_name as author_name " +
            "FROM articles a LEFT JOIN users u ON a.author_id = u.id " +
            "WHERE a.status = 'PUBLISHED' " +
            "ORDER BY a.created_time DESC")
    List<Article> findAll();
    
    // 查询所有文章（包含作者信息，管理员用）
    @Select("SELECT a.*, u.display_name as author_name " +
            "FROM articles a LEFT JOIN users u ON a.author_id = u.id " +
            "ORDER BY a.created_time DESC")
    List<Article> findAllWithAllStatus();
    
    // 根据ID查询文章（包含作者信息）
    @Select("SELECT a.*, u.display_name as author_name " +
            "FROM articles a LEFT JOIN users u ON a.author_id = u.id " +
            "WHERE a.id = #{id}")
    Article findById(Integer id);
    
    // 插入文章（包含新字段）
    @Insert("INSERT INTO articles(title, content, summary, author_id, status, view_count) " +
            "VALUES(#{title}, #{content}, #{summary}, #{authorId}, #{status}, #{viewCount})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Article article);
    
    // 更新文章（包含新字段）
    @Update("UPDATE articles SET title=#{title}, content=#{content}, " +
            "summary=#{summary}, status=#{status}, updated_time = CURRENT_TIMESTAMP " +
            "WHERE id=#{id}")
    void update(Article article);
    
    // 删除文章
    @Delete("DELETE FROM articles WHERE id = #{id}")
    void delete(Integer id);
    
    // 根据作者ID查询文章（包含作者信息）
    @Select("SELECT a.*, u.display_name as author_name " +
            "FROM articles a LEFT JOIN users u ON a.author_id = u.id " +
            "WHERE a.author_id = #{authorId} ORDER BY a.created_time DESC")
    List<Article> findByAuthorId(@Param("authorId") Integer authorId);
    
    // 根据作者ID和状态查询文章
    @Select("SELECT a.*, u.display_name as author_name " +
            "FROM articles a LEFT JOIN users u ON a.author_id = u.id " +
            "WHERE a.author_id = #{authorId} AND a.status = #{status} " +
            "ORDER BY a.created_time DESC")
    List<Article> findByAuthorIdAndStatus(@Param("authorId") Integer authorId, 
                                         @Param("status") String status);
    
    // 检查文章是否存在且属于指定作者
    @Select("SELECT COUNT(*) FROM articles WHERE id = #{id} AND author_id = #{authorId}")
    int countByIdAndAuthor(@Param("id") Integer id, @Param("authorId") Integer authorId);
    
    // 更新文章浏览量
    @Update("UPDATE articles SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(Integer id);
    
    // 更新文章状态
    @Update("UPDATE articles SET status = #{status}, updated_time = CURRENT_TIMESTAMP WHERE id = #{id}")
    void updateStatus(@Param("id") Integer id, @Param("status") String status);
    
    // 获取热门文章（按浏览量排序）
    @Select("SELECT a.*, u.display_name as author_name " +
            "FROM articles a LEFT JOIN users u ON a.author_id = u.id " +
            "WHERE a.status = 'PUBLISHED' " +
            "ORDER BY a.view_count DESC LIMIT #{limit}")
    List<Article> findPopularArticles(@Param("limit") int limit);
    
    // 根据状态查询文章
    @Select("SELECT a.*, u.display_name as author_name " +
            "FROM articles a LEFT JOIN users u ON a.author_id = u.id " +
            "WHERE a.status = #{status} " +
            "ORDER BY a.created_time DESC")
    List<Article> findByStatus(@Param("status") String status);
}