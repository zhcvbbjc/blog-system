package com.blog.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 个人主页响应 DTO
 */
@Data
public class ProfileResponse {

    private UserResponse user;
    private UserStats stats;
    private List<ArticlePreview> recentArticles;
    private List<UserActivity> recentActivities;

    /**
     * 用户统计信息
     */
    @Data
    public static class UserStats {
        private Integer totalArticles;
        private Integer totalLikes;
        private Integer totalComments;
        private Integer totalViews;
        private Integer followers;
        private Integer following;

        public UserStats(Integer totalArticles, Integer totalLikes, Integer totalComments,
                         Integer totalViews, Integer followers, Integer following) {
            this.totalArticles = totalArticles;
            this.totalLikes = totalLikes;
            this.totalComments = totalComments;
            this.totalViews = totalViews;
            this.followers = followers;
            this.following = following;
        }
    }

    /**
     * 文章预览信息
     */
    @Data
    public static class ArticlePreview {
        private Long id;
        private String title;
        private String summary;
        private String slug;
        private Integer viewCount;
        private Integer likeCount;
        private Integer commentCount;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
    }

    /**
     * 用户活动信息
     */
    @Data
    public static class UserActivity {
        private String type; // ARTICLE_CREATED, COMMENT_CREATED, LIKE_ADDED, etc.
        private String description;
        private Long targetId; // 文章ID、评论ID等
        private String targetTitle; // 文章标题等

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime activityTime;

        public UserActivity(String type, String description, Long targetId, String targetTitle, LocalDateTime activityTime) {
            this.type = type;
            this.description = description;
            this.targetId = targetId;
            this.targetTitle = targetTitle;
            this.activityTime = activityTime;
        }
    }
}