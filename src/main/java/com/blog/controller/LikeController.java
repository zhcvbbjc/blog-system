package com.blog.controller;

import com.blog.dto.response.ApiResponse;
import com.blog.entity.User;
import com.blog.security.CustomUserDetails;
import com.blog.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    /**
     * 点赞文章
     */
    @PostMapping("/article/{articleId}")
    public ResponseEntity<ApiResponse<Void>> likeArticle(
            @PathVariable Long articleId,
            Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.toUser();

        likeService.likeArticle(articleId, user.getId());
        return ResponseEntity.ok(ApiResponse.success("点赞成功", null));
    }

    /**
     * 取消点赞文章
     */
    @DeleteMapping("/article/{articleId}")
    public ResponseEntity<ApiResponse<Void>> unlikeArticle(
            @PathVariable Long articleId,
            Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.toUser();

        likeService.unlikeArticle(articleId, user.getId());
        return ResponseEntity.ok(ApiResponse.success("取消点赞成功", null));
    }

    /**
     * 检查是否已点赞
     */
    @GetMapping("/article/{articleId}/status")
    public ResponseEntity<ApiResponse<Boolean>> checkLikeStatus(
            @PathVariable Long articleId,
            Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.toUser();

        boolean isLiked = likeService.isArticleLikedByUser(articleId, user.getId());
        return ResponseEntity.ok(ApiResponse.success("获取成功", isLiked));
    }

    /**
     * 获取文章的点赞数
     */
    @GetMapping("/article/{articleId}/count")
    public ResponseEntity<ApiResponse<Integer>> getArticleLikeCount(@PathVariable Long articleId) {
        int likeCount = likeService.getArticleLikeCount(articleId);
        return ResponseEntity.ok(ApiResponse.success("获取成功", likeCount));
    }
}
