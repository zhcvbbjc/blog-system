package com.blog.repository;

import com.blog.entity.Article;
import com.blog.entity.Comment;
import com.blog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 根据文章查找评论（仅根评论）
     */
    List<Comment> findByArticleAndParentIsNullOrderByCreatedAtDesc(Article article);

    /**
     * 根据文章查找所有评论（包括回复）
     */
    List<Comment> findByArticleOrderByCreatedAtDesc(Article article);

    /**
     * 根据文章分页查找评论
     */
    Page<Comment> findByArticle(Article article, Pageable pageable);

    /**
     * 根据用户查找评论
     */
    List<Comment> findByUserOrderByCreatedAtDesc(User user);

    /**
     * 根据用户ID查找评论
     */
    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    List<Comment> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    /**
     * 根据父评论查找回复
     */
    List<Comment> findByParentOrderByCreatedAtAsc(Comment parent);

    /**
     * 统计文章的评论数量
     */
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.article = :article")
    int countByArticle(@Param("article") Article article);

    /**
     * 统计用户的评论数量
     */
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.user = :user")
    int countByUser(@Param("user") User user);

    /**
     * 查找最新的评论
     */
    @Query("SELECT c FROM Comment c ORDER BY c.createdAt DESC")
    List<Comment> findRecentComments(Pageable pageable);

    /**
     * 根据文章列表查找评论
     */
    @Query("SELECT c FROM Comment c WHERE c.article IN :articles ORDER BY c.createdAt DESC")
    List<Comment> findByArticles(@Param("articles") List<Article> articles);

    /**
     * 查找有回复的评论
     */
    @Query("SELECT c FROM Comment c WHERE SIZE(c.replies) > 0")
    List<Comment> findCommentsWithReplies();

    /**
     * 根据内容搜索评论
     */
    @Query("SELECT c FROM Comment c WHERE c.content LIKE %:keyword%")
    Page<Comment> searchComments(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 删除文章的评论
     */
    void deleteByArticle(Article article);

    /**
     * 删除用户的评论
     */
    void deleteByUser(User user);

    /**
     * 查找用户对特定文章的评论
     */
    @Query("SELECT c FROM Comment c WHERE c.user = :user AND c.article = :article")
    List<Comment> findByUserAndArticle(@Param("user") User user, @Param("article") Article article);
}