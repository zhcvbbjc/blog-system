package com.blog.repository;

import com.blog.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * 根据名称查找标签
     */
    Optional<Tag> findByName(String name);

    /**
     * 根据名称列表查找标签
     */
    List<Tag> findByNameIn(List<String> names);

    /**
     * 检查标签名称是否存在
     */
    boolean existsByName(String name);

    /**
     * 根据名称模糊搜索标签
     */
    List<Tag> findByNameContaining(String name);

    /**
     * 查找热门标签（按使用次数排序）
     */
    @Query("SELECT t FROM Tag t ORDER BY t.usageCount DESC")
    List<Tag> findPopularTags(Pageable pageable);

    /**
     * 查找热门标签（限制数量）
     */
    default List<Tag> findPopularTags(int limit) {
        return findPopularTags(org.springframework.data.domain.PageRequest.of(0, limit));
    }

    /**
     * 查找最近使用的标签
     */
    @Query("SELECT DISTINCT t FROM Tag t JOIN t.articles a ORDER BY a.createdAt DESC")
    List<Tag> findRecentTags(Pageable pageable);

    /**
     * 统计标签数量
     */
    @Query("SELECT COUNT(t) FROM Tag t")
    long countAllTags();

    /**
     * 查找使用次数大于指定值的标签
     */
    List<Tag> findByUsageCountGreaterThan(int minUsageCount);

    /**
     * 查找文章数量最多的标签
     */
    @Query("SELECT t, COUNT(a) as articleCount FROM Tag t JOIN t.articles a GROUP BY t ORDER BY articleCount DESC")
    List<Object[]> findTagsWithArticleCount(Pageable pageable);

    /**
     * 根据文章ID查找标签
     */
    @Query("SELECT t FROM Tag t JOIN t.articles a WHERE a.id = :articleId")
    List<Tag> findByArticleId(@Param("articleId") Long articleId);

    /**
     * 查找未使用的标签（使用次数为0）
     */
    @Query("SELECT t FROM Tag t WHERE t.usageCount = 0")
    List<Tag> findUnusedTags();

    /**
     * 批量删除未使用的标签
     */
    void deleteByUsageCount(int usageCount);

    /**
     * 根据描述搜索标签
     */
    @Query("SELECT t FROM Tag t WHERE t.description LIKE %:keyword%")
    List<Tag> findByDescriptionContaining(@Param("keyword") String keyword);

    /**
     * 查找所有标签名称
     */
    @Query("SELECT t.name FROM Tag t")
    List<String> findAllTagNames();

    /**
     * 分页查找所有标签
     */
    Page<Tag> findAll(Pageable pageable);
}