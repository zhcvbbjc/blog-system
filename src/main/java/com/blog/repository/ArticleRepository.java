package com.blog.repository;

import com.blog.entity.Article;
import com.blog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /**
     * 根据slug查找文章
     */
    Optional<Article> findBySlug(String slug);

    /**
     * 检查slug是否存在
     */
    boolean existsBySlug(String slug);

    /**
     * 根据作者查找文章
     */
    Page<Article> findByAuthor(User author, Pageable pageable);

    /**
     * 根据作者和状态查找文章
     */
    Page<Article> findByAuthorAndStatus(User author, Article.ArticleStatus status, Pageable pageable);

    /**
     * 根据状态查找文章
     */
    Page<Article> findByStatus(Article.ArticleStatus status, Pageable pageable);

    /**
     * 根据标签名称查找文章
     */
    @Query("SELECT a FROM Article a JOIN a.tags t WHERE t.name = :tagName AND a.status = :status")
    Page<Article> findByTagsNameAndStatus(@Param("tagName") String tagName,
                                          @Param("status") Article.ArticleStatus status,
                                          Pageable pageable);

    /**
     * 根据标签名称查找文章（不限制状态）
     */
    @Query("SELECT a FROM Article a JOIN a.tags t WHERE t.name = :tagName")
    Page<Article> findByTagsName(@Param("tagName") String tagName, Pageable pageable);

    /**
     * 根据多个标签查找文章
     */
    @Query("SELECT DISTINCT a FROM Article a JOIN a.tags t WHERE t.name IN :tagNames AND a.status = :status")
    Page<Article> findByTagsInAndStatus(@Param("tagNames") Set<String> tagNames,
                                        @Param("status") Article.ArticleStatus status,
                                        Pageable pageable);

    /**
     * 根据多个标签查找文章，排除指定用户
     */
    @Query("SELECT DISTINCT a FROM Article a JOIN a.tags t WHERE t.name IN :tagNames AND a.author.id <> :userId")
    List<Article> findByTagsInAndUserIdNot(@Param("tagNames") Set<String> tagNames,
                                           @Param("userId") Long userId,
                                           Pageable pageable);

    /**
     * 全文搜索文章（标题和内容）
     */
    @Query("SELECT a FROM Article a WHERE " +
            "(LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.summary) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "a.status = 'PUBLISHED'")
    Page<Article> searchArticles(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 高级搜索
     */
    @Query("SELECT a FROM Article a WHERE " +
            "(:keyword IS NULL OR " +
            "LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:author IS NULL OR LOWER(a.author.username) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
            "a.status = 'PUBLISHED'")
    Page<Article> advancedSearch(@Param("keyword") String keyword,
                                 @Param("author") String author,
                                 Pageable pageable);

    /**
     * 根据作者用户名查找文章
     */
    @Query("SELECT a FROM Article a WHERE a.author.username LIKE %:username% AND a.status = :status")
    Page<Article> findByAuthorUsernameContainingAndStatus(@Param("username") String username,
                                                          @Param("status") Article.ArticleStatus status,
                                                          Pageable pageable);

    /**
     * 查找热门文章（按浏览量排序）
     */
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' ORDER BY a.viewCount DESC")
    List<Article> findPopularArticles(@Param("limit") int limit, Pageable pageable);

    /**
     * 查找热门文章（限制数量）
     */
    @Query(value = "SELECT a FROM Article a WHERE a.status = 'PUBLISHED' ORDER BY a.viewCount DESC")
    List<Article> findPopularArticles(Pageable pageable);

    default List<Article> findPopularArticles(int limit) {
        return findPopularArticles(org.springframework.data.domain.PageRequest.of(0, limit));
    }

    /**
     * 查找最新文章
     */
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' ORDER BY a.publishedAt DESC")
    List<Article> findRecentArticles(Pageable pageable);

    default List<Article> findRecentArticles(int limit) {
        return findRecentArticles(org.springframework.data.domain.PageRequest.of(0, limit));
    }

    /**
     * 统计作者的文章数量
     */
    @Query("SELECT COUNT(a) FROM Article a WHERE a.author = :author")
    int countByAuthor(@Param("author") User author);

    /**
     * 统计作者已发布文章数量
     */
    @Query("SELECT COUNT(a) FROM Article a WHERE a.author = :author AND a.status = 'PUBLISHED'")
    int countPublishedByAuthor(@Param("author") User author);

    /**
     * 查找有AI生成内容的文章
     */
    @Query("SELECT a FROM Article a WHERE a.aiSummary IS NOT NULL OR a.aiTags IS NOT EMPTY")
    Page<Article> findArticlesWithAIContent(Pageable pageable);

    /**
     * 根据标题模糊查找
     */
    List<Article> findByTitleContaining(String title, Pageable pageable);

    /**
     * 查找需要AI处理的文章（没有AI内容且已发布）
     */
    @Query("SELECT a FROM Article a WHERE a.aiSummary IS NULL AND a.status = 'PUBLISHED'")
    List<Article> findArticlesNeedingAIProcessing(Pageable pageable);

    @Query("SELECT DISTINCT a FROM Article a JOIN a.tags t WHERE t.name IN :tagNames AND a.id <> :articleId")
    List<Article> findByTagsInAndIdNot(@Param("tagNames") Set<String> tagNames,
                                       @Param("articleId") Long articleId,
                                       Pageable pageable);

}