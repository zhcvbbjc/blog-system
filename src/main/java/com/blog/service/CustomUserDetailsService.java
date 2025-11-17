package com.blog.service;

import com.blog.entity.User;
import com.blog.repository.UserRepository;
import com.blog.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 自定义用户详情服务
 * 实现 Spring Security 的 UserDetailsService 接口
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 根据用户名加载用户信息
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found with username: {}", username);
                    return new UsernameNotFoundException("用户不存在: " + username);
                });

        if (!user.getEnabled()) {
            log.warn("User account is disabled: {}", username);
            throw new UsernameNotFoundException("用户账户已被禁用: " + username);
        }

        log.debug("Loaded user by username: {}", username);
        return CustomUserDetails.create(user);
//        return user;
    }

    /**
     * 根据用户ID加载用户信息
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new UsernameNotFoundException("用户不存在，ID: " + id);
                });

        if (!user.getEnabled()) {
            log.warn("User account is disabled, id: {}", id);
            throw new UsernameNotFoundException("用户账户已被禁用，ID: " + id);
        }

        log.debug("Loaded user by id: {}", id);
        return CustomUserDetails.create(user);
//        return user;
    }

    /**
     * 根据邮箱加载用户信息
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new UsernameNotFoundException("用户不存在，邮箱: " + email);
                });

        if (!user.getEnabled()) {
            log.warn("User account is disabled, email: {}", email);
            throw new UsernameNotFoundException("用户账户已被禁用，邮箱: " + email);
        }

        log.debug("Loaded user by email: {}", email);
        return CustomUserDetails.create(user);
//        return user;
    }
}