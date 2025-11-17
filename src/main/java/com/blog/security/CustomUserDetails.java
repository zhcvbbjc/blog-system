package com.blog.security;

import com.blog.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 自定义用户详情类
 * 实现 Spring Security 的 UserDetails 接口
 */
@Data
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private Long id;
    private String username;
    private String email;
    private String password;
    private String avatarUrl;
    private String bio;
    private User.UserRole role;
    private Boolean enabled;
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * 从 User 实体创建 CustomUserDetails
     */
    public static CustomUserDetails create(User user) {
        // 将用户角色转换为权限
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getAvatarUrl(),
                user.getBio(),
                user.getRole(),
                user.getEnabled(),
                authorities
        );
    }

    /**
     * 获取用户权限
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * 获取密码
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * 获取用户名
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * 账户是否未过期
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账户是否未锁定
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 凭证是否未过期
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 账户是否启用
     */
    @Override
    public boolean isEnabled() {
        return enabled != null && enabled;
    }

    /**
     * 获取用户 ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 获取用户邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 获取用户头像 URL
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * 获取用户个人简介
     */
    public String getBio() {
        return bio;
    }

    /**
     * 获取用户角色
     */
    public User.UserRole getRole() {
        return role;
    }

    /**
     * 检查用户是否有指定角色
     */
    public boolean hasRole(User.UserRole role) {
        return this.role == role;
    }

    /**
     * 检查用户是否有指定权限
     */
    public boolean hasAuthority(String authority) {
        return authorities.stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }

    /**
     * 转换为 User 实体（用于需要完整用户信息的场景）
     */
    public User toUser() {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setAvatarUrl(avatarUrl);
        user.setBio(bio);
        user.setRole(role);
        user.setEnabled(enabled);
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomUserDetails that = (CustomUserDetails) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "CustomUserDetails{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", enabled=" + enabled +
                '}';
    }
}