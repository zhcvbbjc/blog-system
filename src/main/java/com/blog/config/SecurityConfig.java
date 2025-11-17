package com.blog.config;

import com.blog.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 密码加密器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 安全过滤器链配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF（因为使用 JWT）
                .csrf(csrf -> csrf.disable())

                // 设置会话管理为无状态（因为使用 JWT）
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 配置请求授权
                .authorizeHttpRequests(authz -> authz
                        // 公开接口和页面（不需要认证）
                        .requestMatchers(
                                "/api/auth/**",           // 认证相关接口
                                "/api/articles/public/**", // 公开文章接口
                                "/api/articles/**",       // 文章API（部分公开）
                                "/api/articles/**/count", // 文章统计（公开）
                                "/api/likes/article/**/count", // 点赞数（公开）
                                "/api/comments/article/**", // 评论列表（公开）
                                "/swagger-ui/**",         // Swagger UI
                                "/v3/api-docs/**",        // API 文档
                                "/error",                 // 错误页面
                                "/favicon.ico",           // 网站图标
                                "/css/**",                // CSS静态资源
                                "/js/**",                 // JavaScript静态资源
                                "/images/**",             // 图片静态资源
                                "/uploads/**",            // 上传文件
                                "/",                      // 首页
                                "/home",                  // 首页
                                "/index",                 // 首页
                                "/login",                 // 登录页
                                "/register",              // 注册页
                                "/article/**",            // 文章详情页
                                "/search",                // 搜索页
                                "/tags",                  // 标签页
                                "/tag/**",                // 标签文章列表页
                                "/user/**",               // 用户个人中心页（查看）
                                "/about"                  // 关于页
                        ).permitAll()

                        // 管理接口和页面需要管理员权限
                        .requestMatchers("/api/admin/**", "/admin/**").hasRole("ADMIN")

                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )

                // 添加 JWT 认证过滤器
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}