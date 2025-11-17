package com.blog.dto.response;

import com.blog.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息响应 DTO
 */
@Data
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String avatarUrl;
    private String bio;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // 统计信息
    private Integer articleCount;
    private Integer likeCount;
    private Integer commentCount;

    /**
     * 从 User 实体转换为 UserResponse
     */
    public static UserResponse fromUser(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setBio(user.getBio());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }

    /**
     * 从 User 实体转换，包含统计信息
     */
    public static UserResponse fromUserWithStats(User user, Integer articleCount, Integer likeCount, Integer commentCount) {
        UserResponse response = fromUser(user);
        response.setArticleCount(articleCount);
        response.setLikeCount(likeCount);
        response.setCommentCount(commentCount);
        return response;
    }
}