package com.blog.controller;

import com.blog.dto.request.CommentRequest;
import com.blog.dto.response.ApiResponse;
import com.blog.dto.response.CommentResponse;
import com.blog.entity.User;
import com.blog.exception.BlogException;
import com.blog.security.CustomUserDetails;
import com.blog.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private final RedisTemplate<String, String> redisTemplate;

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

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.toUser();

        // 🔥 Redis 限流：用户每 10 秒只能评论 1 次
        String redisKey = "comment:limit:user:" + user.getId();
        Boolean isAllowed = redisTemplate.opsForValue().setIfAbsent(redisKey, "1", Duration.ofSeconds(10));
        if (Boolean.FALSE.equals(isAllowed)) {
            throw new BlogException("评论过于频繁，请稍后再试", HttpStatus.TOO_MANY_REQUESTS);
        }

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

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.toUser();

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

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.toUser();

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
