package com.blog.security;

import com.blog.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    // JWT 令牌在请求头中的字段名
    private static final String JWT_HEADER = "Authorization";
    private static final String JWT_PREFIX = "Bearer ";

    /**
     * 过滤每个请求，检查 JWT 令牌
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 从请求中获取 JWT 令牌
            String jwt = getJwtFromRequest(request);

            // 验证令牌
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // 从令牌中获取用户名
                String username = tokenProvider.getUsernameFromToken(jwt);

                // 加载用户详情
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                // 创建认证令牌
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 设置认证信息到安全上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Set authentication for user: {}", username);
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context: {}", ex.getMessage());
            // 不抛出异常，让请求继续处理（可能是公开接口）
        }

        // 继续过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 从 HTTP 请求中提取 JWT 令牌
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(JWT_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JWT_PREFIX)) {
            return bearerToken.substring(JWT_PREFIX.length());
        }

        // 也可以从查询参数中获取令牌（可选）
        String tokenFromQuery = request.getParameter("token");
        if (StringUtils.hasText(tokenFromQuery)) {
            return tokenFromQuery;
        }

        // 也可以从 Cookie 中获取令牌（可选）
        // Cookie[] cookies = request.getCookies();
        // if (cookies != null) {
        //     for (Cookie cookie : cookies) {
        //         if ("jwt".equals(cookie.getName())) {
        //             return cookie.getValue();
        //         }
        //     }
        // }

        return null;
    }

    /**
     * 排除某些路径不过滤（可选）
     * 例如：公开接口、静态资源等
     */
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        String path = request.getServletPath();
//
//        // 这些路径不需要 JWT 认证
//        return path.startsWith("/api/auth/") ||
//                path.startsWith("/api/articles/public/") ||
//                path.startsWith("/swagger-ui/") ||
//                path.startsWith("/v3/api-docs/") ||
//                path.startsWith("/webjars/") ||
//                path.equals("/error") ||
//                path.startsWith("/uploads/");
//    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();

        // 这些路径不需要 JWT 认证
        return path.startsWith("/api/auth/login") ||      // 登录
                path.startsWith("/api/auth/register") ||  // 注册
                path.startsWith("/api/auth/refresh") ||   // 刷新token（如果有）
                path.startsWith("/api/articles/public/") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs/") ||
                path.startsWith("/webjars/") ||
                path.equals("/error") ||
                path.startsWith("/uploads/");
        // 注意：移除了 /api/auth/me，因为这个接口需要认证
    }

    /**
     * 获取当前认证的用户名（辅助方法）
     */
    public static String getCurrentUsername() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    /**
     * 获取当前认证的用户 ID（辅助方法）
     */
    public static Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                // 这里需要根据你的 UserDetails 实现来获取用户 ID
                // 假设你的 CustomUserDetails 有 getId() 方法
                if (principal instanceof CustomUserDetails) {
                    return ((CustomUserDetails) principal).getId();
                }
            }
        }
        return null;
    }
}