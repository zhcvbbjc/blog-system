package com.blog.controller;

import com.blog.dto.response.ApiResponse;
import com.blog.dto.response.ProfileResponse;
import com.blog.dto.response.UserActivityResponse;
import com.blog.entity.User;
import com.blog.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /**
     * 获取个人主页信息
     */
    @GetMapping
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        ProfileResponse profile = profileService.getProfile(user.getId());

        return ResponseEntity.ok(ApiResponse.success("获取成功", profile));
    }

    /**
     * 获取其他用户的个人主页信息
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<ProfileResponse>> getUserProfile(@PathVariable Long userId) {
        ProfileResponse profile = profileService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("获取成功", profile));
    }

    /**
     * 获取个人统计数据
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<ProfileResponse.UserStats>> getProfileStats(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        ProfileResponse.UserStats stats = profileService.getUserStats(user.getId());

        return ResponseEntity.ok(ApiResponse.success("获取成功", stats));
    }

    /**
     * 获取用户活动时间线
     */
    @GetMapping("/timeline")
    public ResponseEntity<ApiResponse<Page<UserActivityResponse>>> getUserTimeline(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        User user = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        Page<UserActivityResponse> timeline = profileService.getUserTimeline(user.getId(), pageable);

        return ResponseEntity.ok(ApiResponse.success("获取成功", timeline));
    }

    /**
     * 获取用户最近的活动
     */
    @GetMapping("/recent-activities")
    public ResponseEntity<ApiResponse<List<UserActivityResponse>>> getRecentActivities(
            Authentication authentication,
            @RequestParam(defaultValue = "5") int limit) {

        User user = (User) authentication.getPrincipal();
        List<UserActivityResponse> activities = profileService.getRecentActivities(user.getId(), limit);

        return ResponseEntity.ok(ApiResponse.success("获取成功", activities));
    }
}