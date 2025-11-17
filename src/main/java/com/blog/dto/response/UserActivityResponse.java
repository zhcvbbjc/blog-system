package com.blog.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户活动响应 DTO
 */
@Data
public class UserActivityResponse {

    public enum ActivityType {
        ARTICLE_CREATED,
        ARTICLE_UPDATED,
        ARTICLE_PUBLISHED,
        COMMENT_CREATED,
        LIKE_ADDED,
        PROFILE_UPDATED
    }

    private ActivityType type;
    private String description;
    private Long targetId;
    private String targetTitle;
    private String targetUrl;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activityTime;

    /**
     * 创建文章活动
     */
    public static UserActivityResponse articleCreated(Long articleId, String articleTitle, LocalDateTime time) {
        UserActivityResponse response = new UserActivityResponse();
        response.setType(ActivityType.ARTICLE_CREATED);
        response.setDescription("发布了新文章");
        response.setTargetId(articleId);
        response.setTargetTitle(articleTitle);
        response.setTargetUrl("/articles/" + articleId);
        response.setActivityTime(time);
        return response;
    }

    /**
     * 发表评论活动
     */
    public static UserActivityResponse commentCreated(Long articleId, String articleTitle, LocalDateTime time) {
        UserActivityResponse response = new UserActivityResponse();
        response.setType(ActivityType.COMMENT_CREATED);
        response.setDescription("发表了评论");
        response.setTargetId(articleId);
        response.setTargetTitle(articleTitle);
        response.setTargetUrl("/articles/" + articleId + "#comments");
        response.setActivityTime(time);
        return response;
    }

    /**
     * 点赞活动
     */
    public static UserActivityResponse likeAdded(Long articleId, String articleTitle, LocalDateTime time) {
        UserActivityResponse response = new UserActivityResponse();
        response.setType(ActivityType.LIKE_ADDED);
        response.setDescription("点赞了文章");
        response.setTargetId(articleId);
        response.setTargetTitle(articleTitle);
        response.setTargetUrl("/articles/" + articleId);
        response.setActivityTime(time);
        return response;
    }

    /**
     * 更新个人信息活动
     */
    public static UserActivityResponse profileUpdated(LocalDateTime time) {
        UserActivityResponse response = new UserActivityResponse();
        response.setType(ActivityType.PROFILE_UPDATED);
        response.setDescription("更新了个人信息");
        response.setActivityTime(time);
        return response;
    }
}