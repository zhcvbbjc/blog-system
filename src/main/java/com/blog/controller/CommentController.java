package com.blog.controller;

import com.blog.dto.request.CommentRequest;
import com.blog.dto.response.ApiResponse;
import com.blog.dto.response.CommentResponse;
import com.blog.entity.User;
import com.blog.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 获取文章的评论列表
     */
    @GetMapping("/article/{articleId}")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getArticleComments(@PathVariable Long articleId) {
        List<CommentResponse> comments = commentService.getCommentsByArticleId(articleId);
        return ResponseEntity.ok(ApiResponse.success("获取成功", comments));
    }

    /**
     * 发表评论
     */
    @PostMapping("/article/{articleId}")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @PathVariable Long articleId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        CommentResponse comment = commentService.createComment(articleId, request, user);

        return ResponseEntity.ok(ApiResponse.success("评论发表成功", comment));
    }

    /**
     * 回复评论
     */
    @PostMapping("/{parentId}/reply")
    public ResponseEntity<ApiResponse<CommentResponse>> replyComment(
            @PathVariable Long parentId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        CommentResponse comment = commentService.replyComment(parentId, request, user);

        return ResponseEntity.ok(ApiResponse.success("回复成功", comment));
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long id,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        commentService.deleteComment(id, user);

        return ResponseEntity.ok(ApiResponse.success("评论删除成功", null));
    }

    /**
     * 获取评论详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentResponse>> getComment(@PathVariable Long id) {
        CommentResponse comment = commentService.getCommentById(id);
        return ResponseEntity.ok(ApiResponse.success("获取成功", comment));
    }

    /**
     * 获取用户的评论
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getUserComments(@PathVariable Long userId) {
        List<CommentResponse> comments = commentService.getCommentsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("获取成功", comments));
    }
}