package com.blog.controller;

import com.blog.dto.request.LoginRequest;
import com.blog.dto.request.RegisterRequest;
import com.blog.dto.response.ApiResponse;
import com.blog.dto.response.UserResponse;
import com.blog.entity.User;
import com.blog.security.JwtTokenProvider;
import com.blog.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        UserResponse userResponse = UserResponse.fromUser(user);

        return ResponseEntity.ok(ApiResponse.success("注册成功", userResponse));
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody LoginRequest request) {
        // 认证用户
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 生成令牌
        String token = jwtTokenProvider.generateToken(authentication);
        User user = (User) authentication.getPrincipal();
        UserResponse userResponse = UserResponse.fromUser(user);

        // 返回令牌和用户信息
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", userResponse);

        return ResponseEntity.ok(ApiResponse.success("登录成功", data));
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        UserResponse userResponse = UserResponse.fromUser(user);

        return ResponseEntity.ok(ApiResponse.success("获取成功", userResponse));
    }

    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refreshToken(Authentication authentication) {
        String newToken = jwtTokenProvider.generateToken(authentication);

        Map<String, String> data = new HashMap<>();
        data.put("token", newToken);

        return ResponseEntity.ok(ApiResponse.success("令牌刷新成功", data));
    }

    /**
     * 用户注销
     * 注意：由于 JWT 是无状态的，注销通常在客户端删除令牌
     * 这里可以记录注销日志或加入令牌黑名单（如果实现了的话）
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // 在实际应用中，你可能需要将令牌加入黑名单
        // 或者记录注销日志

        return ResponseEntity.ok(ApiResponse.success("注销成功", null));
    }
}