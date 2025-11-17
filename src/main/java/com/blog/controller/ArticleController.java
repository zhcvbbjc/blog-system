package com.blog.controller;

import com.blog.dto.request.ArticleRequest;
import com.blog.dto.response.ApiResponse;
import com.blog.dto.response.ArticleResponse;
import com.blog.entity.User;
import com.blog.security.CustomUserDetails;
import com.blog.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 获取文章列表（分页）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ArticleResponse>>> getArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(required = false) String tag) {

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ArticleResponse> articles = articleService.getArticles(pageable, tag);
        return ResponseEntity.ok(ApiResponse.success("获取成功", articles));
    }

    /**
     * 获取文章详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleResponse>> getArticle(@PathVariable Long id) {
        ArticleResponse article = articleService.getArticleById(id);
        return ResponseEntity.ok(ApiResponse.success("获取成功", article));
    }

    /**
     * 根据 slug 获取文章详情
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<ArticleResponse>> getArticleBySlug(@PathVariable String slug) {
        ArticleResponse article = articleService.getArticleBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success("获取成功", article));
    }

    /**
     * 创建文章
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ArticleResponse>> createArticle(
            @Valid @RequestBody ArticleRequest request,
            Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.toUser();

        ArticleResponse article = articleService.createArticle(request, user);
        return ResponseEntity.ok(ApiResponse.success("文章创建成功", article));
    }

    /**
     * 更新文章
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleResponse>> updateArticle(
            @PathVariable Long id,
            @Valid @RequestBody ArticleRequest request,
            Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.toUser();

        ArticleResponse article = articleService.updateArticle(id, request, user);
        return ResponseEntity.ok(ApiResponse.success("文章更新成功", article));
    }

    /**
     * 删除文章
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteArticle(
            @PathVariable Long id,
            Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.toUser();

        articleService.deleteArticle(id, user);
        return ResponseEntity.ok(ApiResponse.success("文章删除成功", null));
    }

    /**
     * 发布文章
     */
    @PostMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<ArticleResponse>> publishArticle(
            @PathVariable Long id,
            Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.toUser();

        ArticleResponse article = articleService.publishArticle(id, user);
        return ResponseEntity.ok(ApiResponse.success("文章发布成功", article));
    }

    /**
     * 取消发布文章
     */
    @PostMapping("/{id}/unpublish")
    public ResponseEntity<ApiResponse<ArticleResponse>> unpublishArticle(
            @PathVariable Long id,
            Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.toUser();

        ArticleResponse article = articleService.unpublishArticle(id, user);
        return ResponseEntity.ok(ApiResponse.success("文章取消发布成功", article));
    }

    /**
     * 搜索文章
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ArticleResponse>>> searchArticles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ArticleResponse> articles = articleService.searchArticles(keyword, pageable);

        return ResponseEntity.ok(ApiResponse.success("搜索成功", articles));
    }

    /**
     * 获取热门文章
     */
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<ArticleResponse>>> getPopularArticles(
            @RequestParam(defaultValue = "5") int limit) {

        List<ArticleResponse> articles = articleService.getPopularArticles(limit);
        return ResponseEntity.ok(ApiResponse.success("获取成功", articles));
    }
}
