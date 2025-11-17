package com.blog.dto.response;

import com.blog.entity.Comment;
import com.blog.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论信息响应 DTO
 */
@Data
public class CommentResponse {

    private Long id;
    private String content;
    private UserResponse author;
    private Long articleId;
    private Long parentId;
    private List<CommentResponse> replies;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 从 Comment 实体转换为 CommentResponse
     */
    public static CommentResponse fromComment(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setArticleId(comment.getArticle().getId());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());

        if (comment.getParent() != null) {
            response.setParentId(comment.getParent().getId());
        }

        return response;
    }

    /**
     * 从 Comment 实体转换，包含作者信息
     */
    public static CommentResponse fromCommentWithAuthor(Comment comment, User author) {
        CommentResponse response = fromComment(comment);
        response.setAuthor(UserResponse.fromUser(author));
        return response;
    }

    /**
     * 从 Comment 实体转换，包含作者信息和回复列表
     */
    public static CommentResponse fromCommentWithAuthorAndReplies(Comment comment, User author, List<CommentResponse> replies) {
        CommentResponse response = fromCommentWithAuthor(comment, author);
        response.setReplies(replies);
        return response;
    }
}