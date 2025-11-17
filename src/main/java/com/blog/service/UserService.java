package com.blog.service;

import com.blog.dto.request.RegisterRequest;
import com.blog.dto.request.UpdatePasswordRequest;
import com.blog.dto.request.UpdateProfileRequest;
import com.blog.dto.response.ArticleResponse;
import com.blog.dto.response.UserResponse;
import com.blog.entity.User;
import com.blog.exception.BlogException;
import com.blog.repository.ArticleRepository;
import com.blog.repository.CommentRepository;
import com.blog.repository.LikeRepository;
import com.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    /**
     * 用户注册
     */
    public User register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BlogException("用户名已存在", HttpStatus.BAD_REQUEST);
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BlogException("邮箱已被注册", HttpStatus.BAD_REQUEST);
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setBio(request.getBio());

        return userRepository.save(user);
    }

    /**
     * 根据ID获取用户
     */
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BlogException("用户不存在", HttpStatus.NOT_FOUND));
    }

    /**
     * 根据用户名获取用户
     */
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BlogException("用户不存在", HttpStatus.NOT_FOUND));
    }

    /**
     * 根据邮箱获取用户
     */
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BlogException("用户不存在", HttpStatus.NOT_FOUND));
    }

    /**
     * 更新用户个人信息
     */
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = getUserById(userId);

        // 检查用户名是否被其他用户使用
        if (request.getUsername() != null &&
                !request.getUsername().equals(user.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new BlogException("用户名已被使用", HttpStatus.BAD_REQUEST);
        }

        // 检查邮箱是否被其他用户使用
        if (request.getEmail() != null &&
                !request.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new BlogException("邮箱已被使用", HttpStatus.BAD_REQUEST);
        }

        user.updateProfile(request.getUsername(), request.getEmail(), request.getBio(), null);
        User savedUser = userRepository.save(user);

        return UserResponse.fromUser(savedUser);
    }

    /**
     * 更新用户头像
     */
    public UserResponse updateAvatar(Long userId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new BlogException("头像文件不能为空", HttpStatus.BAD_REQUEST);
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BlogException("只支持图片文件", HttpStatus.BAD_REQUEST);
        }

        User user = getUserById(userId);

        // 上传文件
        String avatarUrl = fileStorageService.storeFile(file, "avatars");

        // 更新用户头像
        user.setAvatarUrl(avatarUrl);
        User savedUser = userRepository.save(user);

        return UserResponse.fromUser(savedUser);
    }

    /**
     * 修改密码
     */
    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        User user = getUserById(userId);

        // 验证当前密码
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BlogException("当前密码不正确", HttpStatus.BAD_REQUEST);
        }

        // 验证新密码和确认密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BlogException("新密码和确认密码不一致", HttpStatus.BAD_REQUEST);
        }

        // 更新密码
        user.changePassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * 获取用户发布的文章
     */
    @Transactional(readOnly = true)
    public Page<ArticleResponse> getUserArticles(Long userId, Pageable pageable) {
        User user = getUserById(userId);
        return articleRepository.findByAuthor(user, pageable)
                .map(article -> ArticleResponse.fromArticleWithAuthor(article, user));
    }

    /**
     * 获取用户点赞的文章
     */
    @Transactional(readOnly = true)
    public Page<ArticleResponse> getUserLikedArticles(Long userId, Pageable pageable) {
        User user = getUserById(userId);
        return likeRepository.findLikedArticlesByUser(user, pageable)
                .map(article -> ArticleResponse.fromArticleWithAuthor(article, article.getAuthor()));
    }

    /**
     * 搜索用户
     */
    @Transactional(readOnly = true)
    public List<UserResponse> searchUsers(String keyword) {
        List<User> users = userRepository.findByUsernameContainingOrEmailContaining(keyword);
        return users.stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户统计信息
     */
    @Transactional(readOnly = true)
    public UserStats getUserStats(Long userId) {
        User user = getUserById(userId);

        int articleCount = articleRepository.countByAuthor(user);
        int likeCount = likeRepository.countByUser(user);
        int commentCount = commentRepository.countByUser(user);

        return new UserStats(articleCount, likeCount, commentCount);
    }

    /**
     * 用户统计信息内部类
     */
    public static class UserStats {
        public final int articleCount;
        public final int likeCount;
        public final int commentCount;

        public UserStats(int articleCount, int likeCount, int commentCount) {
            this.articleCount = articleCount;
            this.likeCount = likeCount;
            this.commentCount = commentCount;
        }
    }
}