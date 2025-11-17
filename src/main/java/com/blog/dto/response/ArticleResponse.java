package com.blog.dto.response;

import com.blog.entity.Article;
import com.blog.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 文章信息响应 DTO
 */
@Data
public class ArticleResponse {

    private Long id;
    private String title;
    private String content;
    private String summary;
    private String slug;
    private String status;
    private Set<String> tags;

    // 作者信息
    private UserResponse author;

    // 统计信息
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;

    // AI 生成内容
    private String aiSummary;
    private Set<String> aiTags;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedAt;

    // 当前用户是否点赞
    private Boolean liked;

    /**
     * 从 Article 实体转换为 ArticleResponse
     */
    public static ArticleResponse fromArticle(Article article) {
        ArticleResponse response = new ArticleResponse();
        response.setId(article.getId());
        response.setTitle(article.getTitle());
        response.setContent(article.getContent());
        response.setSummary(article.getSummary());
        response.setSlug(article.getSlug());
        response.setStatus(article.getStatus().name());
        response.setTags(article.getTagNames());
        response.setViewCount(article.getViewCount());
        response.setLikeCount(article.getLikeCount());
        response.setCommentCount(article.getCommentCount());
        response.setAiSummary(article.getAiSummary());
        response.setAiTags(article.getAiTags());
        response.setCreatedAt(article.getCreatedAt());
        response.setUpdatedAt(article.getUpdatedAt());
        response.setPublishedAt(article.getPublishedAt());
        return response;
    }

    /**
     * 从 Article 实体转换，包含作者信息
     */
    public static ArticleResponse fromArticleWithAuthor(Article article, User author) {
        ArticleResponse response = fromArticle(article);
        response.setAuthor(UserResponse.fromUser(author));
        return response;
    }

    /**
     * 从 Article 实体转换，包含作者信息和点赞状态
     */
    public static ArticleResponse fromArticleWithAuthorAndLikeStatus(Article article, User author, Boolean liked) {
        ArticleResponse response = fromArticleWithAuthor(article, author);
        response.setLiked(liked);
        return response;
    }
}