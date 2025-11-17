package com.blog.controller;

import com.blog.dto.request.UpdatePasswordRequest;
import com.blog.dto.request.UpdateProfileRequest;
import com.blog.dto.response.ApiResponse;
import com.blog.dto.response.ArticleResponse;
import com.blog.dto.response.UserResponse;
import com.blog.entity.User;
import com.blog.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取用户信息
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long userId) {
        UserResponse user = UserResponse.fromUser(userService.getUserById(userId));
        return ResponseEntity.ok(ApiResponse.success("获取成功", user));
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        UserResponse updatedUser = userService.updateProfile(user.getId(), request);

        return ResponseEntity.ok(ApiResponse.success("个人信息更新成功", updatedUser));
    }

    /**
     * 更新用户头像
     */
    @PostMapping("/avatar")
    public ResponseEntity<ApiResponse<UserResponse>> updateAvatar(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        UserResponse updatedUser = userService.updateAvatar(user.getId(), file);

        return ResponseEntity.ok(ApiResponse.success("头像更新成功", updatedUser));
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        userService.updatePassword(user.getId(), request);

        return ResponseEntity.ok(ApiResponse.success("密码修改成功", null));
    }

    /**
     * 获取用户发布的文章
     */
    @GetMapping("/{userId}/articles")
    public ResponseEntity<ApiResponse<Page<ArticleResponse>>> getUserArticles(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ArticleResponse> articles = userService.getUserArticles(userId, pageable);

        return ResponseEntity.ok(ApiResponse.success("获取成功", articles));
    }

    /**
     * 获取用户点赞的文章
     */
    @GetMapping("/{userId}/liked-articles")
    public ResponseEntity<ApiResponse<Page<ArticleResponse>>> getUserLikedArticles(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ArticleResponse> articles = userService.getUserLikedArticles(userId, pageable);

        return ResponseEntity.ok(ApiResponse.success("获取成功", articles));
    }

    /**
     * 搜索用户
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserResponse>>> searchUsers(@RequestParam String keyword) {
        List<UserResponse> users = userService.searchUsers(keyword);
        return ResponseEntity.ok(ApiResponse.success("搜索成功", users));
    }
}