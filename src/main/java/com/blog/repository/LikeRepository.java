package com.blog.repository;

import com.blog.entity.Article;
import com.blog.entity.Like;
import com.blog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    /**
     * 根据用户和文章查找点赞记录
     */
    Optional<Like> findByUserAndArticle(User user, Article article);

    /**
     * 检查用户是否对文章点赞
     */
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Like l WHERE l.user = :user AND l.article = :article AND l.type = 'LIKE'")
    boolean existsByUserAndArticleAndLike(@Param("user") User user, @Param("article") Article article);

    /**
     * 根据用户查找点赞记录
     */
    List<Like> findByUser(User user);

    /**
     * 根据文章查找点赞记录
     */
    List<Like> findByArticle(Article article);

    /**
     * 根据文章和点赞类型查找
     */
    List<Like> findByArticleAndType(Article article, Like.LikeType type);

    /**
     * 统计文章的点赞数量
     */
    @Query("SELECT COUNT(l) FROM Like l WHERE l.article = :article AND l.type = 'LIKE'")
    int countByArticle(@Param("article") Article article);

    /**
     * 统计用户的点赞数量
     */
    @Query("SELECT COUNT(l) FROM Like l WHERE l.user = :user AND l.type = 'LIKE'")
    int countByUser(@Param("user") User user);

    /**
     * 统计文章的点踩数量
     */
    @Query("SELECT COUNT(l) FROM Like l WHERE l.article = :article AND l.type = 'DISLIKE'")
    int countDislikesByArticle(@Param("article") Article article);

    /**
     * 查找用户点赞的文章
     */
    @Query("SELECT l.article FROM Like l WHERE l.user = :user AND l.type = 'LIKE'")
    Page<Article> findLikedArticlesByUser(@Param("user") User user, Pageable pageable);

    /**
     * 查找用户点赞的文章（列表形式）
     */
    @Query("SELECT l.article FROM Like l WHERE l.user.id = :userId AND l.type = 'LIKE'")
    List<Article> findLikedArticlesByUserId(@Param("userId") Long userId);

    /**
     * 查找最近点赞记录
     */
    @Query("SELECT l FROM Like l ORDER BY l.createdAt DESC")
    List<Like> findRecentLikes(Pageable pageable);

    /**
     * 删除用户对文章的点赞
     */
    void deleteByUserAndArticle(User user, Article article);

    /**
     * 删除文章的所有点赞
     */
    void deleteByArticle(Article article);

    /**
     * 删除用户的所有点赞
     */
    void deleteByUser(User user);

    /**
     * 查找热门文章（按点赞数）
     */
    @Query("SELECT l.article, COUNT(l) as likeCount FROM Like l WHERE l.type = 'LIKE' GROUP BY l.article ORDER BY likeCount DESC")
    List<Object[]> findPopularArticlesByLikes(Pageable pageable);

    /**
     * 查找用户点赞时间线
     */
    @Query("SELECT l FROM Like l WHERE l.user = :user ORDER BY l.createdAt DESC")
    Page<Like> findUserLikeTimeline(@Param("user") User user, Pageable pageable);
}